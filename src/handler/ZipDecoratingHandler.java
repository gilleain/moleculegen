package handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openscience.cdk.interfaces.IAtomContainer;

public class ZipDecoratingHandler implements Handler {
	
	private final PrintStreamStringHandler delegate;
	
	private final ZipOutputStream zip;
	
	public ZipDecoratingHandler(String zipFilePath, 
							    String outputFilename, 
							    DataFormat format, 
							    boolean shouldNumberLines) throws IOException {
		this.zip = new ZipOutputStream(new FileOutputStream(zipFilePath));
		this.zip.putNextEntry(new ZipEntry(outputFilename));
		this.delegate = new PrintStreamStringHandler(
				new PrintStream(zip), format, shouldNumberLines);
	}

	@Override
	public void handle(IAtomContainer atomContainer) {
		this.delegate.handle(atomContainer);
	}

	@Override
	public void finish() {
		try {
			this.zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
