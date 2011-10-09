package ru.niir.dispatcher;

public enum NodeType {
	SENSOR, USER, BLIND, INVALID, EMPLOYEE;
	public static NodeType valueOfs(final String s) {
		return SENSOR;
	}
}
