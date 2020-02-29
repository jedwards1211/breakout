package org.breakout.model.calc;

import java.util.LinkedHashMap;

import org.breakout.model.ShotKey;

import com.github.krukow.clj_ds.PersistentVector;

public class CalcTrip {
	public CalcCave cave;
	public String name;
	public final LinkedHashMap<ShotKey, CalcShot> shots = new LinkedHashMap<>();
	public PersistentVector<String> attachedFiles;
}
