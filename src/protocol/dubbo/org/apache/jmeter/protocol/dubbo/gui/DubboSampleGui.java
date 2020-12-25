package org.apache.jmeter.protocol.dubbo.gui;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.dubbo.sampler.DubboSample;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * DubboSampleGui </br>
 * invoke sequence**clearGui()->createTestElement()->modifyTestElement()->configure()**
 */
public class DubboSampleGui extends AbstractSamplerGui {
    
    private static final Logger log = LoggingManager.getLoggerForClass();
    private static final long serialVersionUID = -3248204995359935007L;
    private final DubboPanel panel;

    public DubboSampleGui() {
        super();
        panel = new DubboPanel();
        init();
    }
    
    /**
     * Initialize the interface layout and elements
     */
    private void init() {
        //所有设置panel，垂直布局
        JPanel settingPanel = new VerticalPanel(5, 0);
        settingPanel.setBorder(makeBorder());
        Container container = makeTitlePanel();
        settingPanel.add(container);
        //所有设置panel
        panel.drawPanel(settingPanel);
        //全局布局设置
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(settingPanel,BorderLayout.CENTER);
    }

    /**
     * component title/name
     */
    @Override
    public String getLabelResource() {
        return this.getClass().getSimpleName();
    }

    /**
     * this method sets the Sample's data into the gui
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        log.debug("sample赋值给gui");
        panel.configure(element);
        panel.bundleElement(element);
    }

    /**
     * Create a new sampler. And pass it to the modifyTestElement(TestElement) method.
     */
    @Override
    public TestElement createTestElement() {
        log.debug("创建sample对象");
        //创建sample对象
        DubboSample sample = new DubboSample();
        modifyTestElement(sample);
        return sample;
    }

    /**
     * this method sets the Gui's data into the sample
     */
    @SuppressWarnings("unchecked")
    @Override
    public void modifyTestElement(TestElement element) {
        log.debug("gui数据赋值给sample");
        //给sample赋值
        super.configureTestElement(element);
        panel.modifyTestElement(element);
        panel.bundleElement(element);
    }

    /**
     * sample's name
     */
    @Override
    public String getStaticLabel() {
        return "Dubbo Sample";
    }

    /**
     * clear gui's data
     */
    @Override
    public void clearGui() {
        log.debug("清空gui数据");
        super.clearGui();
        panel.clearGui();
    }

}


