package qlhp;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class SubjectFrame extends JFrame {
	private JTable table;
    private DefaultTableModel model;
    private ArrayList<HocPhan> sub_List;
    private String currentUsername;

    public SubjectFrame(String username) {
        this.currentUsername = username;
        setTitle("Quản lý học phần");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        sub_List = new ArrayList<>();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
                "STT", "ID", "Tên", "Tín chỉ", "Giá"
        });

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel(new FlowLayout());
        JPanel settingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPopupMenu settingMenu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        
        JButton addButton = new JButton("Thêm học phần");
        JButton editButton = new JButton("Sửa học phần");
        JButton deleteButton = new JButton("Xóa học phần");
        JButton searchButton = new JButton("Tìm kiếm học phần");
        JButton settingButton = new JButton("Setting");
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(searchButton);
        settingPanel.add(settingButton);
        settingMenu.add(logoutItem);
        JLabel titleLabel = new JLabel("Danh sách thông tin của học phần: ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        
        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(titleLabel, BorderLayout.CENTER);
        northPanel.add(settingPanel, BorderLayout.EAST);

        add(northPanel, BorderLayout.NORTH);
        //addPopupMenu();
        // Action listeners
        addButton.addActionListener(e -> showAddEditDialog(null));
        editButton.addActionListener(e -> editNguyenVong());
        deleteButton.addActionListener(e -> deleteNguyenVong());
        searchButton.addActionListener(e -> searchNguyenVong());
        settingButton.addActionListener(e -> {
    // Hiển thị menu setting
        	settingMenu.show(settingButton, 0, settingButton.getHeight());
        });
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Đóng cửa sổ hiện tại
                new LoginFrame().setVisible(true); // Hiển thị lại màn hình đăng nhập
            }
        });
        loadDataFromDatabase();
    }

    private SubjectFrame() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private void loadDataFromDatabase() {
    try (Connection connection = DatabaseConnection.getConnection()) {
        // Truy vấn kết hợp hai bảng
        String query = """
                SELECT hp.sub_ID, hp.sub_Name, hp.credit, hp.price
                FROM HocPhan hp
               
                """;

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        sub_List.clear();
        model.setRowCount(0);


        while (resultSet.next()) {
            String sub_ID = resultSet.getString("sub_ID");
            String sub_Name = resultSet.getString("sub_Name");
            int credit = resultSet.getInt("credit");
            float price = resultSet.getFloat("price");

            HocPhan cur = new HocPhan( sub_ID, sub_Name, credit, price);
            sub_List.add(cur);

            // Thêm dữ liệu vào bảng hiển thị
            model.addRow(cur.toArray());
        }

        // Cập nhật số thứ tự
        updateSTT();

        // Đóng statement cập nhật
        // updateStatement.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tải và cập nhật dữ liệu từ cơ sở dữ liệu: " + ex.getMessage());
    }
}

    private void showAddEditDialog(HocPhan cur) {
        JTextField sub_IDField = new JTextField(20);
        JTextField sub_NameField = new JTextField(20);
        JTextField creditField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        
        if (cur != null) {
        	sub_IDField.setText(cur.getSub_ID());
        	sub_NameField.setText(cur.getSub_Name());
        	creditField.setText(String.valueOf(cur.getcredit()));
        	priceField.setText(String.valueOf(cur.getPrice()));
        	sub_IDField.setEditable(false);
        }

        JPanel panel = new JPanel(new GridLayout(10, 2));
        panel.add(new JLabel("ID:"));
        panel.add(sub_IDField);
        panel.add(new JLabel("Tên học phần:"));
        panel.add(sub_NameField);
        panel.add(new JLabel("Số tín chỉ:"));
        panel.add(creditField);
        panel.add(new JLabel("giá:"));
        panel.add(priceField);

        int option = JOptionPane.showConfirmDialog(this, panel, cur == null ? "Thêm Nguyện Vọng" : "Sửa Nguyện Vọng", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String sub_ID = sub_IDField.getText().trim();
            String sub_Name = sub_NameField.getText().trim();
            String credit = creditField.getText().trim();
            String price = priceField.getText().trim();

            if (cur == null) {
                String id;
                Random random = new Random();
                do {
                    id = String.valueOf(random.nextInt(100));
                } while (idDaTonTai(id));
                int creditValue = Integer.parseInt(credit);
                float priceValue = Float.parseFloat(price);
                HocPhan newHP = new HocPhan(id, sub_Name, creditValue ,priceValue);
                sub_List.add(newHP);
                model.addRow(newHP.toArray());
                saveDataToDatabase(newHP, true);
                JOptionPane.showMessageDialog(this, "Thêm học phần thành công!");
            }else {
            	try {
            		int creditValue = Integer.parseInt(credit);
                    float priceValue = Float.parseFloat(price);
            	    HocPhan newHP = new HocPhan(sub_ID, sub_Name, creditValue, priceValue);
            	    int row = table.getSelectedRow();
            	    sub_List.set(row, newHP);

            	    for (int i = 0; i < model.getColumnCount(); i++) {
            	        model.setValueAt(newHP.toArray()[i], row, i);
            	    }
            	    saveDataToDatabase(newHP, false);
            	    JOptionPane.showMessageDialog(this, "Sửa học phần thành công!");
            	} catch (NumberFormatException e) {
            	    JOptionPane.showMessageDialog(this, "Số tín chỉ hoặc giá phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            	}

            }
            updateSTT();
        }
    }
    private boolean idDaTonTai(String id) {
	    for (HocPhan hp : sub_List) {
	        if (hp.getSub_ID().equals(id)) {
	            return true;
	        }
	    }
	    return false;
    }
    
    private void editNguyenVong() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            showAddEditDialog(sub_List.get(row));
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một học phần để sửa!");
        }
    }

    private void deleteNguyenVong() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn học phần này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteDataFromDatabase(sub_List.get(row));
                sub_List.remove(row);
                model.removeRow(row);
                updateSTT();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một nguyện vọng để xóa!");
        }
    }

    private void searchNguyenVong() {
    String searchTerm = JOptionPane.showInputDialog("Nhập id cần tìm:");
    if (searchTerm != null) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Tên học phần");
        model.addColumn("Số tín chỉ");
        model.addColumn("Giá");

        boolean found = false;

        for (HocPhan hp : sub_List) {
            if (hp.getSub_ID().equalsIgnoreCase(searchTerm)) {
                model.addRow(new Object[] {
                	hp.getSub_ID(),
                	hp.getSub_Name(),
                	hp.getcredit(),
                	hp.getPrice()
                });
                found = true;
            }
        }

        if (found) {
            JTable table = new JTable(model);
            // Điều chỉnh chiều cao của các hàng
            table.setRowHeight(30); // Chiều cao mỗi hàng (mặc định là 16)
            
            // Điều chỉnh chiều rộng của các cột
            table.getColumnModel().getColumn(0).setPreferredWidth(100); 
            table.getColumnModel().getColumn(1).setPreferredWidth(150); 
            table.getColumnModel().getColumn(2).setPreferredWidth(150); 
            table.getColumnModel().getColumn(3).setPreferredWidth(100); 
            table.getColumnModel().getColumn(4).setPreferredWidth(100);  

            // Điều chỉnh kích thước bảng
            table.setPreferredScrollableViewportSize(new Dimension(800, 300)); // Kích thước bảng

            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy học phần với id: " + searchTerm);
        }
    }
}

    private void saveDataToDatabase(HocPhan hp, boolean isNew) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query;
            if (isNew) {query = "INSERT INTO HocPhan (subject_ID, subject_Name, tin_chi, price) VALUES (?, ?, ?, ?)";
            }else {
                query = "UPDATE HocPhan " +
                    "SET subject_Name=?, tin_chi=?, price=?" +
                    "WHERE subject_ID=?"; 
            }

            PreparedStatement statement = connection.prepareStatement(query);
            if (isNew) {
            statement.setString(1, hp.getSub_ID());
            statement.setString(2, hp.getSub_Name());
            statement.setInt(3, hp.getcredit());
            statement.setFloat(4, hp.getPrice());
            } else {
            	
            statement.setString(4, hp.getSub_ID());
            statement.setString(1, hp.getSub_Name());
            statement.setInt(2, hp.getcredit());
            statement.setFloat(3, hp.getPrice());
        }
            statement.executeUpdate();
            loadDataFromDatabase();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage());
        }
        
    }

    private void deleteDataFromDatabase(HocPhan hp) {
    try (Connection connection = DatabaseConnection.getConnection()) {
        String query = "DELETE FROM HocPhan WHERE subject_ID = ? ";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, hp.getSub_ID());
        statement.executeUpdate();
        JOptionPane.showMessageDialog(this, "Xóa thành công!");
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu: " + ex.getMessage());
    }
}


    private void updateSTT() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }
    
    private Admin getAdminInfo() {
    Admin admin = null;
    try (Connection connection = DatabaseConnection.getConnection()) {
        String query = "SELECT username, email, password FROM admin WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, currentUsername); // Sử dụng currentUsername
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            admin = new Admin(username, email, password);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin admin: " + ex.getMessage());
    }
    return admin;
}

    private void showAdminInfo() {
    Admin ad = getAdminInfo();
    if (ad != null) {
        // Tạo một hộp thoại hiển thị thông tin admin
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(new JTextField(ad.getAdmin_ID()));
        panel.add(new JLabel("Tên:"));
        panel.add(new JTextField(ad.getAdmin_Name()));
        panel.add(new JLabel("Password:"));
        panel.add(new JTextField(ad.getPass())); // Mật khẩu không cần hiển thị

        // Không cho phép chỉnh sửa mật khẩu
        ((JTextField) panel.getComponent(5)).setEditable(false);

        JOptionPane.showMessageDialog(this, panel, "Thông tin Admin", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin admin.");
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SubjectFrame frame = new SubjectFrame();
            frame.setVisible(true);
        });
    }
}
