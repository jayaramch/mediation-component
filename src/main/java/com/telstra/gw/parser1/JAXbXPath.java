package com.telstra.gw.parser1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.telstra.gw.helper.CommonUtils;
import com.telstra.gw.helper.StringConstants;
import com.telstra.gw.models.SoapXmlEnvelope;

/**
 * Created by Nikhil,Bhavana,Jayaram on 29/04/2018.
 */
@Component
public class JAXbXPath {

	private static final Logger logger = Logger.getLogger(JAXbXPath.class);
	
	private static Properties prop;
    
    static{
        InputStream is = null;
        try {
            prop = new Properties();
            is = JAXbXPath.class.getClassLoader().getResourceAsStream(StringConstants.JAXB_PROPERTIES);
            prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public String getValue(String key) {
		return prop.getProperty(key);
	}
	
	public SoapXmlEnvelope parse(String testMessage, ActiveMQTextMessage jmsMessage) { 
		SoapXmlEnvelope envelope = null;
		try {
			System.setProperty(StringConstants.JAXB_FACTORY, getValue(StringConstants.JAXB_FACTORY));

			JAXBContext jaxbContext = JAXBContext.newInstance(SoapXmlEnvelope.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(testMessage);
			Object object = unmarshaller.unmarshal(reader);
					
			if (object != null) {
				envelope = (SoapXmlEnvelope) object;
				boolean status = CommonUtils.validateSoapHeaders(envelope);
				if (status) {
					CommonUtils.generateConversationId(envelope, jmsMessage);
				} else {
					// generateSoapFault();
				}
			} else {
				// generateSoapFault();
			}
		} catch (Exception e) {
			// System.out.println(e.getStackTrace());
			e.printStackTrace();
		} finally {
			// Release the objects
		}
		return envelope;
	}
}
