package org.andork.func;

public class IntegerStringBimapper implements Bimapper<Integer, String> {
	public static final IntegerStringBimapper	instance	= new IntegerStringBimapper();

	private IntegerStringBimapper() {

	}

	@Override
	public String map(Integer in) {
		return in == null ? null : in.toString();
	}

	@Override
	public Integer unmap(String out) {
		return out == null ? null : Integer.valueOf(out);
	}
}
