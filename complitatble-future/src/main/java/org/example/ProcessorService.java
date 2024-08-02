package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProcessorService {

	public void processAsync() {
		LocalDateTime start = LocalDateTime.now();
		System.out.println("Async Processing stated at "+start);
		List<CompletableFuture<?>> completableFutures= new ArrayList<>();
		CompletableFuture<List<User>> user =
			CustomCompletableFuture.supplyAsyncWithRetry(()->getUsers(100),2)
				.thenApply(res ->{
					return res;
				});

		CompletableFuture<List<Department>> department =
			CustomCompletableFuture.supplyAsyncWithRetry(()->getDepartment(100),2)
				.thenApply(res ->{
					return res;
				});

		completableFutures.add(user);
		completableFutures.add(department);

		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
		LocalDateTime end = LocalDateTime.now();
		System.out.println("Async Time taken: "+java.time.Duration.between(start, end).toMillis()+"ms");
	}

	public void processSync() {
		LocalDateTime start = LocalDateTime.now();
		System.out.println("Sync Processing stated at "+start);
		getUsers(100);
		getDepartment(100);
		LocalDateTime end = LocalDateTime.now();
		System.out.println("Syn Time taken: "+java.time.Duration.between(start, end).toMillis() +"ms");
	}

	private List<User> getUsers(int noOfRecords) {
		List<User> users = new ArrayList<>();
		for(int i=0;i<noOfRecords;i++) {
			User user = new User(i,"user-"+i,"department-"+i);
			users.add(user);
		}
		Util.sleep(1000);
		return users;
	}

	private List<Department> getDepartment(int noOfRecords) {
		List<Department> departments = new ArrayList<>();
		for(int i=0;i<noOfRecords;i++) {
			Department department = new Department(i,"department-"+i);
			departments.add(department);
		}
		Util.sleep(1000);
		return departments;
	}
}

class User {
	private int id;
	private String name;
	private String department;

	public User(int id, String name, String department) {
		this.id = id;
		this.name = name;
		this.department = department;
	}
}

class Department {
	private int id;
	private String name;

	public Department(int id, String name) {
		this.id = id;
		this.name = name;
	}
}

class Util {
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
