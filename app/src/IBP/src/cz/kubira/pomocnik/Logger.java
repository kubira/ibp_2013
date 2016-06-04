package cz.kubira.pomocnik;

public class Logger extends Thread {
	private int s;

	public Logger(int sekund) {

		this.s = sekund;
	}

	@Override
	public void run() {
		int i = 0;

		try {
			while(true) {
				Thread.sleep(s*1000);
				System.out.println("I: "+i);
				i++;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}