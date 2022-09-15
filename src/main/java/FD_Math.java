import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.axis.AxisType;
import net.imglib2.algorithm.fft2.FFTConvolution;
import net.imglib2.type.numeric.real.FloatType;
import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.command.Command;
import org.scijava.ui.UIService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Plugin(type = Command.class, menuPath = "Process>FFT>FD Math")
public class FD_Math implements Command{

    @Parameter
    private UIService uiService;

    @Parameter
    private DatasetService datasetService;

    @Parameter(label = "Image 1: ", persist = false)
    private Dataset dataset1;

    @Parameter(label = "Operation: ", choices = {"Convolve", "Correlate"})
    private String operation;

    @Parameter(label = "Image 2: ", persist = false)
    private Dataset dataset2;

    @Parameter(label = "Output: ")
    private String outputName;

    @Parameter(type = ItemIO.OUTPUT)
    private Dataset result;

    @Override
    public void run(){

        AxisType[] axisTypes = new AxisType[dataset1.numDimensions()];
        for (int i = 0; i < dataset1.numDimensions(); ++i) {
            axisTypes[i] = dataset1.axis(i).type();
        }
        result = datasetService.create(new FloatType(), dataset1.dimensionsAsLongArray(), outputName, axisTypes);

        ExecutorService service = Executors.newCachedThreadPool();
        FFTConvolution convolver = new FFTConvolution(dataset1, dataset2, service);
        convolver.setOutput(result);

        if(operation.compareTo("Correlate") == 0){
            convolver.setComputeComplexConjugate(true);
        }

        convolver.convolve();
    }
}
