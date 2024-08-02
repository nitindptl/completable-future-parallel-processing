package org.example;

public class Main {
	public static void main(String[] args) {

		ProcessorService processorService = new ProcessorService();

		processorService.processSync();
		processorService.processAsync();
	}
}
