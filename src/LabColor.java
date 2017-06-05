import java.awt.Color;

public class LabColor extends Color {
	
	private double[] labComponents;
	
	public LabColor(){
		super(0);
		labComponents = rgb2lab(0, 0, 0);
	}

	public LabColor(int red, int green, int blue) {
		super(red, green, blue);
		labComponents = rgb2lab(red, green, blue);
	}
	
	public LabColor(int rgb){
		super(rgb);
		labComponents = rgb2lab(getRed(), getGreen(), getBlue());
	}
	
	public static final double[] rgb2lab(int red, int green, int blue){
		double r = red / 255.0;
		double g = green / 255.0;
		double b = blue / 255.0;
		double x, y, z;

		r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
		g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
		b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);
		x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.95047;
		y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.00000;
		z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.08883;

		x = (x > 0.008856) ? Math.pow(x, 1.0/3.0) : (7.787 * x) + 16.0/116.0;
		y = (y > 0.008856) ? Math.pow(y, 1.0/3.0) : (7.787 * y) + 16.0/116.0;
		z = (z > 0.008856) ? Math.pow(z, 1.0/3.0) : (7.787 * z) + 16.0/116.0;
		double[] lab = {(116.0 * y) - 16.0, 500.0 * (x - y), 200.0 * (y - z)};
		return lab;
	}
	
	public static final double[] rgb2lab(int[] components){
		return rgb2lab(components[0], components[1], components[2]);
	}
	
	public static final int[] lab2rgb(double l, double a, double b){
		  double y = (l + 16) / 116;
		  double x = a / 500 + y;
		  double z = y - b / 200;
		  double red, green, blue;

		  x = 0.95047 * ((x * x * x > 0.008856) ? x * x * x : (x - 16/116) / 7.787);
		  y = 1.00000 * ((y * y * y > 0.008856) ? y * y * y : (y - 16/116) / 7.787);
		  z = 1.08883 * ((z * z * z > 0.008856) ? z * z * z : (z - 16/116) / 7.787);

		  red = x *  3.2406 + y * -1.5372 + z * -0.4986;
		  green = x * -0.9689 + y *  1.8758 + z *  0.0415;
		  blue = x *  0.0557 + y * -0.2040 + z *  1.0570;

		  red = (red > 0.0031308) ? (1.055 * Math.pow(red, 1/2.4) - 0.055) : 12.92 * red;
		  green = (green > 0.0031308) ? (1.055 * Math.pow(green, 1/2.4) - 0.055) : 12.92 * green;
		  blue = (blue > 0.0031308) ? (1.055 * Math.pow(blue, 1/2.4) - 0.055) : 12.92 * blue;
		  int[] rgb = {(int) (Math.max(0, Math.min(1, red)) * 255), 
		          (int) (Math.max(0, Math.min(1, green)) * 255), 
		          (int) (Math.max(0, Math.min(1, blue)) * 255)};
		  return rgb;
	}
	
	public static final int[] lab2rgb(double[] components){
		return lab2rgb(components[0], components[1], components[2]);
	}
	
	public static final double getDeltaE94(LabColor labColor1, LabColor labColor2){
		double l1 = labColor1.getL();
		double a1 = labColor1.getA();
		double b1 = labColor1.getB();
		
		double l2 = labColor2.getL();
		double a2 = labColor2.getA();
		double b2 = labColor2.getB();
		double whtL = 1;
		double whtC = 1;
		double whtH = 1;               //Weighting factors

		double xC1 = Math.sqrt( (a1 * a1) + (b1 * b1));
		double xC2 = Math.sqrt( (a2 * a2) + (b2 * b2));
		double xDL = l2 - l1;
		double xDC = xC2 - xC1;
		double xDE = Math.sqrt((( 1l - l2) * (l1 - l2))
		          + ((a1 - a2) * (a1 - a2))
		          + ((b1 - b2) * (b1 - b2)));

		double xDH = (xDE * xDE) - (xDL * xDL) - (xDC * xDC);
		if ( xDH > 0 ) {
		   xDH = Math.sqrt( xDH );
		}
		else {
		   xDH = 0;
		}
		double xSC = 1 + ( 0.045 * xC1 );
		double xSH = 1 + ( 0.015 * xC1 );
		xDL /= whtL;
		xDC /= whtC * xSC;
		xDH /= whtH * xSH;

		return Math.sqrt(xDL * xDL + xDC * xDC + xDH * xDH);
	}
	
	public static final double getDeltaE(LabColor labColor1, LabColor labColor2){
		double l1 = labColor1.getL();
		double a1 = labColor1.getA();
		double b1 = labColor1.getB();
		
		double l2 = labColor2.getL();
		double a2 = labColor2.getA();
		double b2 = labColor2.getB();
		
		return Math.sqrt( (l2-l1)*(l2-l1) + (a2-a1)*(a2-a1) + (b2-b1)*(b2-b1));
	}
	
	public static final double getDeltaErgb(LabColor labColor1, LabColor labColor2){
		double r1 = labColor1.getRed();
		double g1 = labColor1.getGreen();
		double b1 = labColor1.getBlue();
		
		double r2 = labColor2.getRed();
		double g2 = labColor2.getGreen();
		double b2 = labColor2.getBlue();
		
		return Math.sqrt( (r2-r1)*(r2-r1) + (g2-g1)*(g2-g1) + (b2-b1)*(b2-b1));
	}
	
	public double getL(){
		return labComponents[0];
	}
	
	public double getA(){
		return labComponents[1];
	}
	
	public double getB(){
		return labComponents[2];
	}
}
