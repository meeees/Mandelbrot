
//this will also need to be written with BigDecimal for higher zooms
//this is written in the style of BigInteger/BigDecimal
public class Complex {

	private double real;
	private double imag;
	
	public Complex()
	{
		real = 0;
		imag = 0;
	}
	
	public Complex(double r, double i)
	{
		real = r;
		imag = i;
	}
	
	public Complex add(Complex c1)
	{
		return new Complex(real + c1.real, imag + c1.imag);
	}
	
	public Complex sub(Complex c1)
	{
		return new Complex(real - c1.real, imag + c1.imag);
	}
	
	public Complex scale(double scalar)
	{
		return new Complex(real * scalar, imag * scalar);
	}
	
	//foil method
	public Complex mult(Complex c1)
	{
		return new Complex(real * c1.real + imag * c1.imag * -1, real * c1.imag + c1.real * imag);
	}
	
	//we could do division but we shouldn't need it for this
	
	public double getMagnitudeSq()
	{
		return real * real + imag * imag;
	}
	
	public double getMagnitude()
	{
		return Math.sqrt(real * real + imag * imag);
	}
	
	public boolean equals(Object o)
	{
		Complex c = (Complex) o;
		return real == c.real && imag == c.imag;
	}
	
	public String toString()
	{
		return String.format("%f + %fi", real, imag);
	}
	
	public double getReal()
	{
		return real;
	}
	
	public double getImag()
	{
		return imag;
	}
}
