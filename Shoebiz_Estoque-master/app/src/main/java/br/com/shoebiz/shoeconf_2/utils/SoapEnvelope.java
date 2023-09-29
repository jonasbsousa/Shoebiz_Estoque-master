package br.com.shoebiz.shoeconf_2.utils;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class SoapEnvelope extends SoapSerializationEnvelope {

    public SoapEnvelope(int version) {
        super(version);
    }

    public void write(XmlSerializer writer) throws IOException {
        writer.setPrefix("i", xsi);
        writer.setPrefix("d", xsd);
        writer.setPrefix("c", enc);
        writer.setPrefix("soap", env); // era writer.setPrefix("v", env)
        writer.startTag(env, "Envelope");
        writer.startTag(env, "Header");
        writeHeader(writer);
        writer.endTag(env, "Header");
        writer.startTag(env, "Body");
        writeBody(writer);
        writer.endTag(env, "Body");
        writer.endTag(env, "Envelope");
    }
}