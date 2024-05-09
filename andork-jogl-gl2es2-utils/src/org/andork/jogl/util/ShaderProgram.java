package org.andork.jogl.util;

import java.io.PrintStream;

import org.andork.jogl.JoglResource;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.glsl.ShaderCode;

public class ShaderProgram implements JoglResource {
	com.jogamp.opengl.util.glsl.ShaderProgram wrapped = new com.jogamp.opengl.util.glsl.ShaderProgram();

	public boolean linked() {
		return wrapped.linked();
	}

	public boolean inUse() {
		return wrapped.inUse();
	}

	public int program() {
		return wrapped.program();
	}

	public int id() {
		return wrapped.id();
	}

	public void destroy(GL2ES2 gl) {
		wrapped.destroy(gl);
	}

	public void release(GL2ES2 gl) {
		wrapped.release(gl);
	}

	public void release(GL2ES2 gl, boolean destroyShaderCode) {
		wrapped.release(gl, destroyShaderCode);
	}

	public void add(ShaderCode shaderCode) throws GLException {
		wrapped.add(shaderCode);
	}

	public boolean contains(ShaderCode shaderCode) {
		return wrapped.contains(shaderCode);
	}

	public ShaderCode getShader(int id) {
		return wrapped.getShader(id);
	}

	public final boolean init(GL2ES2 gl) {
		return wrapped.init(gl);
	}

	public boolean add(GL2ES2 gl, ShaderCode shaderCode, PrintStream verboseOut) {
		return wrapped.add(gl, shaderCode, verboseOut);
	}

	public boolean replaceShader(GL2ES2 gl, ShaderCode oldShader, ShaderCode newShader, PrintStream verboseOut) {
		return wrapped.replaceShader(gl, oldShader, newShader, verboseOut);
	}

	public boolean link(GL2ES2 gl, PrintStream verboseOut) {
		return wrapped.link(gl, verboseOut);
	}

	public boolean equals(Object obj) {
		return wrapped.equals(obj);
	}

	public int hashCode() {
		return wrapped.hashCode();
	}

	public StringBuilder toString(StringBuilder sb) {
		return wrapped.toString(sb);
	}

	public String toString() {
		return wrapped.toString();
	}

	public boolean validateProgram(GL2ES2 gl, PrintStream verboseOut) {
		return wrapped.validateProgram(gl, verboseOut);
	}

	public void useProgram(GL2ES2 gl, boolean on) {
		wrapped.useProgram(gl, on);
	}

	public void notifyNotInUse() {
		wrapped.notifyNotInUse();
	}

	public void dumpSource(PrintStream out) {
		wrapped.dumpSource(out);
	}

	@Override
	public void dispose(GL2ES2 gl) {
		wrapped.destroy(gl);
	}
}
