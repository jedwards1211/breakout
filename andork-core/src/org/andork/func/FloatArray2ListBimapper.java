package org.andork.func;

import java.util.ArrayList;
import java.util.List;

public class FloatArray2ListBimapper implements Bimapper<float[], List<? extends Number>> {
	public static final FloatArray2ListBimapper	instance	= new FloatArray2ListBimapper();

	private FloatArray2ListBimapper() {

	}

	@Override
	public List<Float> map(float[] in) {
		List<Float> result = new ArrayList<Float>();
		for (float f : in) {
			result.add(f);
		}
		return result;
	}

	@Override
	public float[] unmap(List<? extends Number> out) {
		float[] result = new float[out.size()];
		int k = 0;
		for (Number f : out) {
			result[k++] = f.floatValue( );
		}
		return result;
	}

}
