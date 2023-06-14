import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
// import java.awt.Color;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class compression{
    int width=352;
    int height=288;
    BufferedImage oriImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int[] oneChannelPxs = new int[width * height];


    private void readFile(String fileName) {
        FileInputStream input = null;
        byte[] content = new byte[width * height];

        try {
            input = new FileInputStream(fileName);
            // Reads up to size bytes of data from this input stream into an array of bytes.
            input.read(content, 0, content.length);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte r = content[y * width + x];
                    byte g = r;
                    byte b = r;

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    Color color = new Color(pix, true);
                    oneChannelPxs[y * width + x] = color.getRed();
                    oriImg.setRGB(x, y, pix);
                }
            }

            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static double euclideanDistance(int[] a, int[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    public void kmeans(int m,int n){
        double double_m=(double)m;
        double double_w = (double) width;
        double double_h = (double) height;
        int k=n;
        int maxIterations = 100;
        double tolerance = 2.0;// the max distance that centroids shift
        int numDimensions = m;
        int[][] centroids = new int[n][m];
        int data[][]= new int[(int)Math.ceil(double_w*double_h/double_m)][m];
        //initialize data[][]
        int x = (int)Math.sqrt(Double.valueOf(m));
        for(int i=0; i<(int)Math.ceil(double_w*double_h/double_m);i++){
            if(m==2){
                for(int j=0;j<m;j++){
                    if ((i*m+j)<width*height){
                        data[i][j]=oneChannelPxs[i*m+j];
                    }
                }
            }
            else{
                for(int j=0;j<m;j++){
                    // int x = (int)Math.sqrt(Double.valueOf(m));
                    // System.out.println(((i*x)/width)*x*width+(i*x)%width+(j/x)*width+(j%x));
                    if(((i*x)/width)*x*width+(i*x)%width+(j/x)*width+(j%x)<width*height)
                        data[i][j]=oneChannelPxs[((i*x)/width)*x*width+(i*x)%width+(j/x)*width+(j%x)];
                    else
                        data[i][j]=oneChannelPxs[width*height-1];
                }
            }
        }
        // randomly initialize centroids
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            int randomIndex = random.nextInt(data.length);
            centroids[i] = Arrays.copyOf(data[randomIndex], numDimensions);
        }

        // run k-means algorithm
        int[] labels = new int[data.length];
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            // assign each data point to closest centroid
            for (int i = 0; i < data.length; i++) {
                double minDistance = Double.MAX_VALUE;
                int label = -1;
                for (int j = 0; j < k; j++) {
                    double distance = euclideanDistance(data[i], centroids[j]);
                    if (distance < minDistance) {
                        minDistance = distance;
                        label = j;
                    }
                }
                labels[i] = label;
            }

            // update centroids
            int[][] newCentroids = new int[k][numDimensions];
            int[] counts = new int[k];
            for (int i = 0; i < data.length; i++) {
                int label = labels[i];
                for (int j = 0; j < numDimensions; j++) {
                    newCentroids[label][j] += data[i][j];
                }
                counts[label]++;
            }
            for (int i = 0; i < k; i++) {
                if (counts[i] != 0) {
                    for (int j = 0; j < numDimensions; j++) {
                        newCentroids[i][j] /= counts[i];
                    }
                }
            }

            // check for convergence
            double maxCentroidShift = 0;
            for (int i = 0; i < k; i++) {
                double centroidShift = euclideanDistance(centroids[i], newCentroids[i]);
                if (centroidShift > maxCentroidShift) {
                    maxCentroidShift = centroidShift;
                }
            }
            centroids = newCentroids;
            if (maxCentroidShift < tolerance) {
                break;
            }
        }

        for(int i=0; i<data.length; i++){
            data[i]=centroids[labels[i]];
            // System.out.println(data[i][0]);
            // System.out.println(data[i][1]);
        }

        if(m==2){
            for(int i=0; i<(int)Math.ceil(double_w*double_h/double_m);i++){
                for(int j=0;j<m;j++){
                    if ((i*m+j)<width*height){
                        oneChannelPxs[i*m+j]=data[i][j];
                        // System.out.println(data[i][j]);
                    }
                }
            }
        }
        else{
            for(int i=0; i<(int)Math.ceil(double_w*double_h/double_m);i++){
                for(int j=0;j<m;j++){
                    if (((i*x)/width)*x*width+(i*x)%width+(j/x)*width+(j%x)<width*height){
                        oneChannelPxs[((i*x)/width)*x*width+(i*x)%width+(j/x)*width+(j%x)]=data[i][j];
                        // System.out.println(data[i][j]);
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < width; z++) {
                byte r = (byte)oneChannelPxs[y * width + z];
                byte g = r;
                byte b = r;

                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                // System.out.println(pix);

                img.setRGB(z, y, pix);
            }
        }
    }
    public void showIms(String[] args){
        JFrame frame;
        JLabel lbIm1;
        JLabel lbIm2;

		// for(int y = 0; y < height; y++){

		// 	for(int x = 0; x < width; x++){

		// 		// byte a = (byte) 255;
		// 		byte r = (byte) 255;
		// 		byte g = (byte) 255;
		// 		byte b = (byte) 255;

		// 		int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
		// 		//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
		// 		img.setRGB(x,y,pix);
		// 	}
		// }


		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("Original image (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("Image after compression (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(oriImg));
		lbIm2 = new JLabel(new ImageIcon(img));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		frame.setVisible(true);
	}
    public static void main(String[] args) {
        compression c= new compression();
        int m = Integer.valueOf(args[1]);
        int n = Integer.valueOf(args[2]);
        // System.out.println("The first parameter was: " );
        // "image1-onechannel.rgb"
        c.readFile(args[0]);
        c.kmeans(m, n);
        c.showIms(args);
	}
}