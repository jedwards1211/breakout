package org.andork.func;

public class IntegerBimapper implements Bimapper<Integer, Object> {
	public static final IntegerBimapper	instance	= new IntegerBimapper();

	private IntegerBimapper() {

	}


	@Override
	public Object map(Integer in) {
		return in;
	}

	@Override
	public Integer unmap(Object out) {
		if (out instanceof Integer) {
			return (Integer) out;
		}
		return out == null ? null : Integer.valueOf(out.toString());
	}

}
