package org.apache.jmeter.protocol.dubbo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledChoice;

public class MypluginGUI extends AbstractSamplerGui {

    private static final long serialVersionUID = 240L;

    private JTextField domain;
    private JTextField port;
    private JTextField contentEncoding;
    private JTextField path;
    private JCheckBox useKeepAlive;
    private JLabeledChoice method;

    // area区域
    private JSyntaxTextArea postBodyContent = JSyntaxTextArea.getInstance(30, 50);
    // 滚动条
    private JTextScrollPane textPanel = JTextScrollPane.getInstance(postBodyContent);
    private JLabel textArea = new JLabel("Message");

    private JPanel getDomainPanel() {
        domain = new JTextField(10);
        JLabel label = new JLabel("IP"); // $NON-NLS-1$
        label.setLabelFor(domain);

        JPanel panel = new HorizontalPanel();
        panel.add(label, BorderLayout.WEST);
        panel.add(domain, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getPortPanel() {
        port = new JTextField(10);

        JLabel label = new JLabel(JMeterUtils.getResString("web_server_port")); // $NON-NLS-1$
        label.setLabelFor(port);

        JPanel panel = new HorizontalPanel();
        panel.add(label, BorderLayout.WEST);
        panel.add(port, BorderLayout.CENTER);

        return panel;
    }

    protected JPanel getContentEncoding() {

        // CONTENT_ENCODING
        contentEncoding = new JTextField(10);
        JLabel contentEncodingLabel = new JLabel("contentEncoding"); // $NON-NLS-1$
        contentEncodingLabel.setLabelFor(contentEncoding);

        JPanel panel = new HorizontalPanel();
        panel.setMinimumSize(panel.getPreferredSize());
        panel.add(Box.createHorizontalStrut(5));

        panel.add(contentEncodingLabel,BorderLayout.WEST);
        panel.add(contentEncoding,BorderLayout.CENTER);
        panel.setMinimumSize(panel.getPreferredSize());
        return panel;
    }

    protected Component getPath() {
        path = new JTextField(15);

        JLabel label = new JLabel(JMeterUtils.getResString("path")); //$NON-NLS-1$
        label.setLabelFor(path);

        JPanel pathPanel = new HorizontalPanel();
        pathPanel.add(label);
        pathPanel.add(path);

        JPanel panel = new HorizontalPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(pathPanel);

        return panel;
    }

    protected Component getMethodAndUseKeepAlive() {
        useKeepAlive = new JCheckBox(JMeterUtils.getResString("use_keepalive")); // $NON-NLS-1$
        useKeepAlive.setFont(null);
        useKeepAlive.setSelected(true);
        JPanel optionPanel = new HorizontalPanel();
        optionPanel.setMinimumSize(optionPanel.getPreferredSize());
        optionPanel.add(useKeepAlive);
        String Marry[] = { "GET", "POST" };
        method = new JLabeledChoice(JMeterUtils.getResString("method"), // $NON-NLS-1$
                Marry, true, false);
        // method.addChangeListener(this);
        JPanel panel = new HorizontalPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(optionPanel,BorderLayout.WEST);
        panel.add(method,BorderLayout.WEST);
        return panel;
    }

    protected Component getpostBodyContent() {

        JPanel panel = new HorizontalPanel();
        JPanel ContentPanel = new VerticalPanel();
        JPanel messageContentPanel = new JPanel(new BorderLayout());
        messageContentPanel.add(this.textArea, BorderLayout.NORTH);
        messageContentPanel.add(this.textPanel, BorderLayout.CENTER);
        ContentPanel.add(messageContentPanel);
        ContentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Content"));
        panel.add(ContentPanel);
        return panel;
    }

    public MypluginGUI() {
        super();
        init();
    }

    private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or
        // final)
        creatPanel();
    }

    public void creatPanel() {
        JPanel settingPanel = new VerticalPanel(5, 0);
        settingPanel.add(getDomainPanel());
        settingPanel.add(getPortPanel());
        settingPanel.add(getContentEncoding());
        settingPanel.add(getPath());
        settingPanel.add(getMethodAndUseKeepAlive());
        settingPanel.add(getpostBodyContent());
        JPanel dataPanel = new JPanel(new BorderLayout(5, 0));

        dataPanel.add(settingPanel, BorderLayout.NORTH);
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH); // Add the standard title
        add(dataPanel, BorderLayout.CENTER);
    }

    /*
     * 创建一个新的Sampler，然后将界面中的数据设置到这个新的Sampler实例中
     * */
    @Override
    public TestElement createTestElement() {
        // TODO Auto-generated method stub
        MyPluginSampler sampler = new MyPluginSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public String getLabelResource() {
        // TODO Auto-generated method stub
        throw new IllegalStateException("This shouldn't be called");
        // return "example_title";
        // 从messages_zh_CN.properties读取
    }

    @Override
    public String getStaticLabel() {
        return "Qiao jiafei";
    }

    /*
     * 把界面的数据移到Sampler中，与configure方法相反
     * */
    @Override
    public void modifyTestElement(TestElement arg0) {
        // TODO Auto-generated method stub
        arg0.clear();
        configureTestElement(arg0);

        arg0.setProperty(MyPluginSampler.domain, domain.getText());
        arg0.setProperty(MyPluginSampler.port, port.getText());
        arg0.setProperty(MyPluginSampler.contentEncoding, contentEncoding.getText());
        arg0.setProperty(MyPluginSampler.path, path.getText());
        arg0.setProperty(MyPluginSampler.method, method.getText());
        arg0.setProperty(MyPluginSampler.postBodyContent, postBodyContent.getText());
        arg0.setProperty(new BooleanProperty(MyPluginSampler.useKeepAlive, useKeepAlive.isSelected()));

    }

    /*
     * reset新界面的时候调用，这里可以填入界面控件中需要显示的一些缺省的值
     * */
    @Override
    public void clearGui() {
        super.clearGui();

        domain.setText("");
        port.setText("");
        contentEncoding.setText("");
        path.setText("");
        method.setText("GET");
        postBodyContent.setText("");
        useKeepAlive.setSelected(true);

    }

    /*
     * 把Sampler中的数据加载到界面中
     * */
    @Override
    public void configure(TestElement element) {

        super.configure(element);
        // jmeter运行后，保存参数，不然执行后，输入框会情况

        domain.setText(element.getPropertyAsString(MyPluginSampler.domain));
        port.setText(element.getPropertyAsString(MyPluginSampler.port));
        contentEncoding.setText(element.getPropertyAsString(MyPluginSampler.contentEncoding));
        path.setText(element.getPropertyAsString(MyPluginSampler.path));
        method.setText("GET");
        postBodyContent.setText(element.getPropertyAsString(MyPluginSampler.postBodyContent));
        useKeepAlive.setSelected(true);

    }

}