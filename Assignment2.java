import java.math.BigInteger;
import java.util.Random;
import java.security.*;
import javax.crypto.*;
import java.io.File;
import java.io.*;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

class Assignment2{
	static BigInteger d;
	static BigInteger x;
	static BigInteger y;

	public static BigInteger primeValue(){
		SecureRandom rand = new SecureRandom();
		BigInteger  pv;
		pv = BigInteger.probablePrime(512,rand);
		return pv;
	}

	public static BigInteger product(BigInteger p, BigInteger q){
		return p.multiply(q);
	}

	public static BigInteger mod(BigInteger sum,BigInteger p)
    {
    	
    	BigInteger zero = new BigInteger("0");
    	int result;
    	result =  sum.compareTo(zero);
    	return sum.remainder(p);
    }

	public static BigInteger modexp(BigInteger base, BigInteger exp,BigInteger m)
    {
        BigInteger sum= BigInteger.ONE;
        BigInteger zero = new BigInteger("0");
        BigInteger one = new BigInteger("1");

        
        for(int dx=0; dx <exp.bitLength(); ++dx ){
            sum = sum.multiply(base).mod(m);
            if (exp.and(one).equals(one))
                sum =mod(sum.multiply(base),m);
                exp = exp.shiftRight(1);
                base=mod(sum.multiply(base),m);
        }
        return sum;                             
    }

    public static BigInteger  gcd(BigInteger n, BigInteger m)
	{
		BigInteger zero = new BigInteger("0");
		if (m.equals(zero))  
			return n;
		else
			return gcd(m,n.mod(m));
	}

	public static BigInteger modInverse(BigInteger a,BigInteger m)
	{
		
		BigInteger in=extecl(a,m);
		return((in.mod(m)).add(m)).mod(m);	
	}

	public static  BigInteger extecl(BigInteger e,BigInteger n)
	{
		
		BigInteger zero =new BigInteger("0");

		if(n.equals(zero))
		{
			d=e;
			x=BigInteger.ONE;
			y=BigInteger.ZERO;
		}
		else
		{	if (!n.equals(zero)){
			extecl(n,e.mod(n));
			BigInteger temp=x;
			x=y;
			y=temp.subtract((e.divide(n)).multiply(y));
		}
			
		}
		return x;
	}
	
	public static BigInteger decryptExpo(BigInteger p,BigInteger q)
	{
		BigInteger e =new BigInteger("65537");
		BigInteger phi = phi_n(p, q);
		BigInteger d=modInverse(e,phi);
		return d;
	}

	public static BigInteger phi_n(BigInteger p, BigInteger q ){
		BigInteger phiN;
		phiN= p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		return phiN;
	}
	
    public static BigInteger decrypt(BigInteger a, BigInteger b, BigInteger p, BigInteger q) {
        BigInteger s = modexp(a, b.mod(p.subtract(BigInteger.ONE)), p);
        BigInteger t = modexp(a, b.mod(q.subtract(BigInteger.ONE)), q);
        BigInteger qpIn = modInverse(q, p);

    
        return t.add(q.multiply((qpIn.multiply(s.subtract(t))).mod(p)));
    }

	public static void main(String [] args){	

		BigInteger q, p, n;
		q = primeValue();
	    p=  primeValue();
	    n = product(q,p); 
		String fileName = null;
		BigInteger de= decryptExpo(p,q);

/*
	TESTER; if the value returns 53 then the decryptionExponention is correct

		BigInteger r= new BigInteger("7");
		BigInteger y= new BigInteger("11");
		BigInteger f= decryptExpo(r,y);
		System.out.println(f);
*/
	
			
        if(args.length < 1){
                fileName = "AssignmentTwo.class";
            } else {
                fileName = args[0];
            }

        try{
        	MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger plaintext = new BigInteger(1, md.digest(Files.readAllBytes(Paths.get(args[0]))));
            BigInteger signedDigest = decrypt(plaintext, de, p, q);
            String s = signedDigest.toString(16);
            String t = n.toString(16);
            File moduloN  = new File("Modulus.txt");
            File signedD  = new File("Signature.txt");
            FileWriter fileWriter = new FileWriter(moduloN);
            FileWriter fileWriter2 = new FileWriter(signedD);
            fileWriter2.write(s);
            fileWriter.write(t);
            fileWriter.flush();
            fileWriter.close();
            fileWriter2.flush();
            fileWriter2.close();
            System.out.println(" Successfully imported into:   Signature.txt & Modulus.txt " );
            
        } 
        catch(Exception error){
            error.printStackTrace();
       	}      	
	}
}
