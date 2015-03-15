package handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.openscience.cdk.interfaces.IAtomContainer;

public class ZipDecoratingHandler implements GenerateHandler {
	
	private final PrintStreamStringHandler delegate;
	
	private final ZipOutputStream zip;
	
	public ZipDecoratingHandler(String zipFilePath, 
							    String outputFilename, 
							    DataFormat format, 
							    boolean shouldNumberLines, 
							    boolean shouldShowParent) throws IOException {
		this.zip = new ZipOutputStream(new FileOutputStream(zipFilePath));
		this.zip.putNextEntry(new ZipEntry(outputFilename));
		this.delegate = new PrintStreamStringHandler(
				new PrintStream(zip), format, shouldNumberLines, shouldShowParent);
	}

	@Override
	public void handle(IAtomContainer parent, IAtomContainer child) {
		this.delegate.handle(parent, child);
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
