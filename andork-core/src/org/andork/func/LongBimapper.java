package org.andork.func;

public class LongBimapper implements Bimapper<Long, Object> {
	public static final LongBimapper	instance	= new LongBimapper();

	private LongBimapper() {

	}

	@Override
	public Object map(Long in) {
		return in;
	}

	@Override
	public Long unmap(Object out) {
		if (out instanceof Long) {
			return (Long) out;
		}
		return out == null ? null : Long.valueOf(out.toString());
	}

}
