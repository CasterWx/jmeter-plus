package org.apache.jmeter.protocol.dubbo.gui;

import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;

/**
 * DubboDefaultPanel
 */
public class DubboDefaultPanel extends DubboCommonPanel {

    public void drawPanel(JPanel parent) {
        parent.add(drawConfigCenterSettingsPanel());
        parent.add(drawRegistrySettingsPanel());
        parent.add(drawProtocolSettingsPanel());
        parent.add(drawConsumerSettingsPanel());
    }

    public void configure(TestElement element) {
        configureConfigCenter(element);
        configureRegistry(element);
        configureProtocol(element);
        configureConsumer(element);
    }

    public void modifyTestElement(TestElement element) {
        modifyConfigCenter(element);
        modifyRegistry(element);
        modifyProtocol(element);
        modifyConsumer(element);
    }

    public void clearGui() {
        clearConfigCenter();
        clearRegistry();
        clearProtocol();
        clearConsumer();
    }
}
