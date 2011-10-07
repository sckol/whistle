package ru.niir.protowhistle.util;

public abstract class StoppableRunnable implements Runnable {
	protected boolean finished = false;

	protected abstract void onStop();

	protected abstract void onReset();

	protected abstract void whileRun();

	public void run() {
		finished = false;
		onReset();
		while (!finished) {
			whileRun();
		}
	}

	public void stop() {
		finished = true;
		onStop();
	}

	public boolean isFinished() {
		return finished;
	}
}
