package org.andork.func;

public class DoubleBimapper implements Bimapper<Double, Object> {
	public static final DoubleBimapper	instance	= new DoubleBimapper();

	private DoubleBimapper() {}

	@Override
	public Object map(Double in) {
		return in;
	}

	@Override
	public Double unmap(Object out) {
		if (out instanceof Double) {
			return (Double) out;
		}
		return out == null ? null : Double.valueOf(out.toString());
	}

}
