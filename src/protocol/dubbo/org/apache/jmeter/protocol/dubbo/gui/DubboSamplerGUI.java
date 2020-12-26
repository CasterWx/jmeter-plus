package org.apache.jmeter.protocol.dubbo.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.dubbo.core.DubboInvokeService;
import org.apache.jmeter.protocol.dubbo.core.JsonFormatUtil;
import org.apache.jmeter.protocol.dubbo.core.ZookeeperClient;
import org.apache.jmeter.protocol.dubbo.core.ZookeeperService;
import org.apache.jmeter.protocol.dubbo.sampler.DubboSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author AntzUhl
 * @Date 2020/12/26 13:43
 * @Description
 */
public class DubboSamplerGUI extends AbstractSamplerGui implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(DubboSamplerGUI.class);

    private static final String ZK_DUBBO_PREFIX = "/dubbo";

    private static List<String> includes = Lists.newArrayList();

    private static String DEFAULT_ZK_ADDR = JMeterUtils.getPropDefault("default_zk_addr", "");

    private JTextField zookeeperAddress;
    private JButton connectZk;
    private JComboBox<String> applicationName;
    private JComboBox<String> serviceName;
    private JComboBox<String> ipAddress;
    private JComboBox<String> methodName;
    private String[] columnNames = {"paramType", "paramValue"};
    DefaultTableModel tableModel = new DefaultTableModel(null, columnNames);
    JTable paramTable = new JTable(tableModel);
    private JSyntaxTextArea responseContent = JSyntaxTextArea.getInstance(100, 50);
    private JTextScrollPane textPanel = JTextScrollPane.getInstance(responseContent);

    JScrollPane scrollPane = new JScrollPane(paramTable);
    public DubboSamplerGUI() {
        super();
        init();
    }

    protected Component getResponseBodyContent() {
        JPanel panel = new HorizontalPanel();
        JPanel ContentPanel = new VerticalPanel();
        JPanel messageContentPanel = new JPanel(new BorderLayout());
        messageContentPanel.add(new JLabel("Response"), BorderLayout.NORTH);
        messageContentPanel.add(this.textPanel, BorderLayout.CENTER);
        ContentPanel.add(messageContentPanel);
        ContentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Response Area"));
        panel.add(ContentPanel);
        return panel;
    }

    private JPanel getZookeeperAddressPanel() {
        zookeeperAddress = new JTextField(7);
        zookeeperAddress.setText(DEFAULT_ZK_ADDR);
        JLabel label = new JLabel("Zookeeper Address：");
        label.setLabelFor(zookeeperAddress);
        connectZk = new JButton("Connect");
        JPanel panel = new HorizontalPanel();
        panel.add(label, BorderLayout.WEST);
        panel.add(zookeeperAddress, BorderLayout.CENTER);
        panel.add(connectZk, BorderLayout.EAST);
        return panel;
    }

    private JPanel getApplicationName() {
        // application name
        applicationName = new JComboBox<>();
        applicationName.setEditable(false);
        applicationName.setFont(new Font("Arial",Font.PLAIN,16));
        applicationName.setSize(new Dimension(400, 30));
        applicationName.setPreferredSize(new Dimension(400, 30));
        applicationName.addItemListener(new AppItemChangeListener());
        JLabel appLabel = new JLabel("Application：");
        appLabel.setBorder(new EmptyBorder(0,0, 0, 10));
        appLabel.setLabelFor(applicationName);

        ipAddress = new JComboBox<>();
        ipAddress.setEditable(false);
        ipAddress.setFont(new Font("Arial",Font.PLAIN,16));
        ipAddress.setSize(new Dimension(300, 30));
        ipAddress.setPreferredSize(new Dimension(300, 30));
        JLabel ipLabel = new JLabel("IP Address：");
        ipLabel.setLabelFor(ipAddress);
        ipLabel.setBorder(new EmptyBorder(0,20, 0, 10));
        JPanel panel = new JPanel();
        panel.add(appLabel, BorderLayout.WEST);
        panel.add(applicationName, BorderLayout.CENTER);
        panel.add(ipLabel, BorderLayout.WEST);
        panel.add(ipAddress, BorderLayout.CENTER);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        return panel;
    }

    private JPanel getServiceName() {
        // service name
        serviceName = new JComboBox<>();
        serviceName.setEditable(false);
        serviceName.setFont(new Font("Arial",Font.PLAIN,16));
        serviceName.setSize(new Dimension(600, 30));
        serviceName.setPreferredSize(new Dimension(600, 30));
        serviceName.addItemListener(new MethodItemChangeListener());
        JLabel serviceLabel = new JLabel("ServiceName：");
        serviceLabel.setLabelFor(serviceName);
        serviceLabel.setBorder(new EmptyBorder(0,0, 0, 10));
        JPanel panel = new JPanel();
        panel.add(serviceLabel, BorderLayout.WEST);
        panel.add(serviceName, BorderLayout.CENTER);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        return panel;
    }

    private JPanel getMethodName() {
        // Method name
        methodName = new JComboBox<>();
        methodName.setEditable(false);
        methodName.setFont(new Font("Arial",Font.PLAIN,16));
        methodName.setSize(new Dimension(600, 30));
        methodName.setPreferredSize(new Dimension(600, 30));
        JLabel label = new JLabel("Method Name：");
        label.setLabelFor(methodName);
        label.setBorder(new EmptyBorder(0,0, 0, 10));

        JButton addButton = new JButton("增加");
        addButton.setBorder(new EmptyBorder(0,20, 0, 10));
        JButton delButton = new JButton("删除");
        delButton.setBorder(new EmptyBorder(0,20, 0, 10));
        JButton reqButton = new JButton("Request");
        addButton.setBorder(new EmptyBorder(0,20, 0, 10));
        addButton.addActionListener(this);
        delButton.addActionListener(this);
        reqButton.addActionListener(this);
        JPanel panel = new JPanel();
        panel.add(label, BorderLayout.WEST);
        panel.add(methodName, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.CENTER);
        panel.add(delButton, BorderLayout.CENTER);
        panel.add(reqButton, BorderLayout.CENTER);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        return panel;
    }

    private void init() {
        creatPanel();
        String showApplication = JMeterUtils.getProperty("show_application");
        if (StringUtils.isNotBlank(showApplication)) {
            includes = Arrays.stream(showApplication.split(",")).collect(Collectors.toList());
        }
    }

    public void creatPanel() {

        JPanel settingPanel = new VerticalPanel(7, 100);
        settingPanel.add(getZookeeperAddressPanel());
        settingPanel.add(getApplicationName());
        settingPanel.add(getServiceName());
        settingPanel.add(getMethodName());
        connectZk.addActionListener(new ZkConnectButtonListener());
        paramTable.setFont(new Font("Arial", Font.PLAIN, 22));// 设置表格字体
        paramTable.setRowHeight(24);
        paramTable.setPreferredScrollableViewportSize(new Dimension(200, 200));
//        paramTable.setSize(new Dimension(settingPanel.WIDTH, 200));
        settingPanel.add(scrollPane);
        settingPanel.add(getResponseBodyContent());
        JPanel dataPanel = new JPanel(new BorderLayout(7, 0));

        dataPanel.add(settingPanel, BorderLayout.NORTH);
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title
        add(dataPanel, BorderLayout.CENTER);
    }

    @Override
    public String getLabelResource() {
        throw new IllegalStateException("This shouldn't be called");
    }

    @Override
    public TestElement createTestElement() {
        DubboSampler sampler = new DubboSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public String getStaticLabel() {
        return "Dubbo Sampler";
    }

    @Override
    public void modifyTestElement(TestElement element) {
        element.clear();
        configureTestElement(element);
        element.setProperty(DubboSampler.zookeeper, zookeeperAddress.getText());
        element.setProperty(DubboSampler.application, (String) applicationName.getSelectedItem());
        element.setProperty(DubboSampler.service, (String) serviceName.getSelectedItem());
        element.setProperty(DubboSampler.method, (String) methodName.getSelectedItem());
        element.setProperty(DubboSampler.ip, (String) ipAddress.getSelectedItem());
        int rowCount = paramTable.getRowCount();
        String []paramTypes = new String[rowCount];
        String []paramValues = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            String paramType= paramTable.getValueAt(i, 0).toString();
            String paramValue= paramTable.getValueAt(i, 1).toString();
            paramTypes[i] = paramType;
            paramValues[i] = paramValue;
        }
        element.setProperty(DubboSampler.paramTypes, StringUtils.join(paramTypes, ","));
        element.setProperty(DubboSampler.paramValues, StringUtils.join(paramValues, ","));
    }

    // appList
    Set<String> applicationList = Sets.newConcurrentHashSet();
    // applicationName - serviceName
    private Map<String, Set<String>> appService = Maps.newConcurrentMap();
    // applicationName - ipAddr
    private Map<String, Set<String>> appIpAddr = Maps.newConcurrentMap();

    // serviceName - methodName
    private Map<String, Set<String>> serviceMethod = Maps.newConcurrentMap();


    /*
     * reset新界面的时候调用，这里可以填入界面控件中需要显示的一些缺省的值
     * */
    @Override
    public void clearGui() {
        super.clearGui();
        zookeeperAddress.setText(DEFAULT_ZK_ADDR);
    }

    /*
     * 把Sampler中的数据加载到界面中
     * */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        zookeeperAddress.setText(element.getPropertyAsString(DubboSampler.zookeeper));
        applicationName.setSelectedItem(element.getPropertyAsString(DubboSampler.application));
        serviceName.setSelectedItem(element.getPropertyAsString(DubboSampler.service));
        ipAddress.setSelectedItem(element.getPropertyAsString(DubboSampler.ip));
        methodName.setSelectedItem(element.getPropertyAsString(DubboSampler.method));
    }

    class ZkConnectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Connect")) {
                String zkAddr = zookeeperAddress.getText();
                if (StringUtils.isNotBlank(zkAddr)) {
                    ZooKeeper zkClient = ZookeeperClient.zkClient(zkAddr, 30000);
                    ZookeeperService zookeeperService = new ZookeeperService(zkClient);
                    try {
                        // 获取服务列表
                        List<String> dubboList = zookeeperService.getChildren(ZK_DUBBO_PREFIX);

                        if (CollectionUtils.isNotEmpty(dubboList)) {
                            // 拼接后缀，获取providers列表，并且解析
                            dubboList.stream().parallel().forEach(item -> {
                                String addr = ZK_DUBBO_PREFIX + "/" + item + "/providers";
                                try {
                                    List<String> providerList = zookeeperService.getChildren(addr);
                                    if (CollectionUtils.isNotEmpty(providerList)) {
                                        providerList.stream().forEach(provider -> {
                                            if (StringUtils.isNotBlank(provider)) {
                                                try {
                                                    provider = URLDecoder.decode(provider, "GBK");
                                                    String[] split = provider.split("&");
                                                    // 只需要分组0的注册信息以及appname，method
                                                    String[] dubboInfo = split[0].split("/");
                                                    if (dubboInfo.length < 2) {
                                                        return;
                                                    }
                                                    String ipAddr = dubboInfo[2];
                                                    String interfaceInfo = item;
                                                    // appName
                                                    String appName = "";
                                                    Pattern ps = Pattern.compile("application=.*?&");
                                                    Matcher ms = ps.matcher(provider);
                                                    if (ms.find()) {
                                                        appName = ms.group().replace("application=", "").replace("&", "");
                                                    } else {
                                                        return;
                                                    }
                                                    // 过滤掉一部分app
                                                    if (CollectionUtils.isNotEmpty(includes) && !includes.contains(appName)) {
                                                        return;
                                                    }
                                                    // method
                                                    String methodInfo = "";
                                                    Pattern p = Pattern.compile("methods=.*?&");
                                                    Matcher m = p.matcher(provider);
                                                    if (m.find()) {
                                                        methodInfo = m.group().replace("methods=", "").replace("&", "");
                                                    }
                                                    // appName ipAddr interfaceInfo methodInfo
                                                    applicationList.add(appName);
                                                    // interfaceInfo
                                                    Set<String> appSrv = appService.get(appName);
                                                    if (appSrv != null) {
                                                        appSrv.add(interfaceInfo);
                                                    } else {
                                                        appSrv = Sets.newHashSet();
                                                        appSrv.add(interfaceInfo);
                                                    }
                                                    appService.put(appName, appSrv);
                                                    // ipAddr
                                                    Set<String> appIds = appIpAddr.get(appName);
                                                    if (appIds != null) {
                                                        appIds.add(ipAddr);
                                                    } else {
                                                        appIds = Sets.newHashSet();
                                                        appIds.add(ipAddr);
                                                    }
                                                    appIpAddr.put(appName, appIds);
                                                    // methodInfo
                                                    Set<String> serviceMethods = serviceMethod.get(interfaceInfo);
                                                    if (serviceMethods != null) {
                                                        serviceMethods.add(methodInfo);
                                                    } else {
                                                        serviceMethods = Sets.newHashSet();
                                                        serviceMethods.add(methodInfo);
                                                    }
                                                    serviceMethod.put(interfaceInfo, serviceMethods);
                                                } catch (UnsupportedEncodingException ex) {
                                                    ex.printStackTrace();
                                                }
                                                System.out.println(provider);
                                            }
                                        });
                                    }
                                } catch (KeeperException ex) {
                                    ex.printStackTrace();
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            });
                            applicationName.removeAllItems();
                            applicationList.stream().forEach(i->{
                                applicationName.addItem(i);
                            });
                        }
                        System.out.println("done");
                    } catch (KeeperException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


    class AppItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                String item = (String) event.getItem();
                // appName change
                Set<String> ipList = appIpAddr.get(item);
                Set<String> serviceList = appService.get(item);
                if (CollectionUtils.isEmpty(ipList) || CollectionUtils.isEmpty(serviceList)) {
                    return;
                }
                ipAddress.removeAllItems();
                serviceName.removeAllItems();
                ipList.stream().forEach(i -> {
                    ipAddress.addItem(i);
                });
                serviceList.stream().forEach(i -> {
                    serviceName.addItem(i);
                });
            }
        }
    }


    class MethodItemChangeListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                String item = (String) event.getItem();
                // appName change
                Set<String> methodList = serviceMethod.get(item);
                if (CollectionUtils.isEmpty(methodList)) {
                    return;
                }
                methodName.removeAllItems();
                methodList.stream().forEach(i -> {
                    String[] split = i.split(",");
                    for (int j = 0; j < split.length; j++) {
                        methodName.addItem(split[j]);
                    }
                });
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("增加")) {
            // 如果点击"增加行"按钮后，会在表中增加一行
            tableModel.addRow(new Vector());
        }
        if (e.getActionCommand().equals("Request")) {
            // 发送dubbo请求
            String interfaceName = (String) serviceName.getSelectedItem();
            String ipAddr = (String) ipAddress.getSelectedItem();
            String invokeMethodName = (String) methodName.getSelectedItem();
            int rowCount = paramTable.getRowCount();
            String []paramTypes = new String[rowCount];
            String []paramValues = new String[rowCount];
            for (int i = 0; i < rowCount; i++) {
                String paramType= paramTable.getValueAt(i, 0).toString();
                String paramValue= paramTable.getValueAt(i, 1).toString();
                paramTypes[i] = paramType;
                paramValues[i] = paramValue;
            }
            if (StringUtils.isNotBlank(interfaceName) && StringUtils.isNotBlank(ipAddr) && StringUtils.isNotBlank(invokeMethodName)) {
                String result = DubboInvokeService.callDubbo(interfaceName, ipAddr, invokeMethodName, paramTypes, paramValues);
                if (StringUtils.isNotBlank(result)) {
                    String formatJson = JsonFormatUtil.formatJson(result);
                    responseContent.setText(formatJson);
                }
            }
        }
        if (e.getActionCommand().equals("删除")) {
            // 如果点击"删除行"按钮后，会在表中删除所选中的一行，并且设置下一行为当前行
            int num = tableModel.getRowCount() - 1;
            int rowcount = paramTable.getSelectedRow();
            // getRowCount返回行数，rowcount<0代表已经没有行了
            if (rowcount >= 0) {
                tableModel.removeRow(rowcount);
                tableModel.setRowCount(num);
                /**
                 * 删除行比较简单，只要用DefaultTableModel的removeRow方法即可
                 * 删除行完毕后必须重新设置列数，也就是使用DefaultTableModel的setRowCount()方法来设置当前行
                 */
            }
        }
        paramTable.revalidate();
    }
}
