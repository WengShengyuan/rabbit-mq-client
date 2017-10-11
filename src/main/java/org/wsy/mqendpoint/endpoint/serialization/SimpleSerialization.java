package org.wsy.mqendpoint.endpoint.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.Hessian2StreamingInput;
import com.caucho.hessian.io.Hessian2StreamingOutput;

public class SimpleSerialization {

	public static byte[] toByte(Object object) throws Exception {
		Hessian2StreamingOutput oos = null;
	    try {
	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      oos = new Hessian2StreamingOutput(bos);
	      oos.writeObject(object);
	      byte[] b = bos.toByteArray();
	      return b;
	    } finally {
	      if (oos != null) {
	        oos.close();
	      }
	    }
	}

	public static Object toObject(byte[] objectBytes) throws Exception {
		Hessian2StreamingInput ois = null;
	    try {
	      ByteArrayInputStream bis = new ByteArrayInputStream(objectBytes);
	      ois = new Hessian2StreamingInput(bis);
	      return ois.readObject();
	    } finally {
	      if (ois != null) {
	        ois.close();
	      }
	    }
	}

}
