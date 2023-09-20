import com.formdev.flatlaf.themes.FlatMacLightLaf;
import vuls.check;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class toolPanel {
    private JPanel rootPanel;
    private JLabel tagetLabel;
    private JTextField targetTextField;
    private JButton checkButton;
    private JTabbedPane tabbledPane1;
    private JTextArea checkTextArea;
    private JPanel checkPanel;
    private JComboBox vulComboBox;
    public Map<String, Object> deserializeMap = new HashMap<>();

//    public Object proxy() {
//        Object proxyConfig;
//        String proxySwitch = String.valueOf("123");
//        String proxyType = String.valueOf("123");
//        String proxyIP = "127.0.0.1";
//        String proxyPort = "8080";
//        String proxyUser = "admin";
//        String proxyPass = "pass";
//        if ("开启".equals(proxySwitch)) {
//            if ("HTTP".equals(proxyType)) {
//                proxyConfig = new httpRequest.ProxyConfig(Proxy.Type.HTTP, proxyIP, Integer.parseInt(proxyPort), proxyUser, proxyPass);
//            } else {
//                proxyConfig = new httpRequest.ProxyConfig(Proxy.Type.SOCKS, proxyIP, Integer.parseInt(proxyPort), proxyUser, proxyPass);
//            }
//        } else {
//            proxyConfig = null;
//        }
//        return proxyConfig;
//    }

    private class VulnerabilityCheckWorker extends SwingWorker<Void, String> {
        private final String vulName;
        private final String url;

        public VulnerabilityCheckWorker(String vulName, String url) {
            this.vulName = vulName;
            this.url = url;
        }
        @Override
        protected Void doInBackground() throws Exception {
            check cp = new check();

            if ("All".equals(vulName)) {
                String[] vulArray = {"dahua_dss_fileDown", "大华智慧园区文件上传"};
                for (String vul : vulArray) {
                    SwingUtilities.invokeLater(() -> checkTextArea.append(String.format("开始检测漏洞 [%s]\n", vul)));
                    try {
                        String result = cp.checkAction(deserializeMap, vul, url);
                        SwingUtilities.invokeLater(() -> checkTextArea.append(result + "\n\n"));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                SwingUtilities.invokeLater(() -> checkTextArea.append("全部检测完毕～\n\n"));
            } else {
                try {
                    String result = cp.checkAction(deserializeMap, vulName, url);
                    SwingUtilities.invokeLater(() -> checkTextArea.setText(result));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            return null;
        }


        @Override
        protected void process(List<String> chunks) {
            for (String chunk : chunks) {
                checkTextArea.append(chunk);
            }
        }
    }
    public void check() {
        checkButton.addActionListener(e -> {
            // 清除文本区域
            checkTextArea.setText("");
            String url = targetTextField.getText();

            // 如果URL不以'/'结尾，添加它
            if (!url.endsWith("/")) {
                url += "/";
            }

            // 更新文本字段
//            targetTextField.setText(url);
//            checkTextArea.append(targetTextField.getText());

            // 获取所选的漏洞
            String vulName = String.valueOf(vulComboBox.getSelectedItem());

            // 创建并执行SwingWorker
            new VulnerabilityCheckWorker(vulName, url).execute();
        });
    }
    public toolPanel() {
        //设置鼠标悬浮提示
        targetTextField.setToolTipText("http://127.0.0.1:8888");
        // 设置JTextArea为不可编辑
        checkTextArea.setEditable(false);

        //说明信息
        checkTextArea.setText("1.大华DSS文件下载\n" +
                "2.大华智慧园区devicePoint_addImgIco文件上传");

        vulComboBox.addItem("All");
        vulComboBox.addItem("大华智慧园区文件上传");
        vulComboBox.addItem("dahua_dss_fileDown");

        check();

    }
    public static void main(String[] args) {
        FlatMacLightLaf.setup();
        JFrame frame = new JFrame("大华系列漏洞检测利用工具 by joyboy");
        frame.setSize(750, 450);
        frame.setContentPane(new toolPanel().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
