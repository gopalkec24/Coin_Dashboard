package com.gopal.test;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayQueueTest {
	
	public static void main(String[] args ){
		int n=4;
		ArrayBlockingQueue<Integer> arrayQueue = new ArrayBlockingQueue<Integer>(n);
		
		/*arrayQueue.add(1);
		System.out.println("size of queue is "+arrayQueue.size());
		arrayQueue.add(3);
		arrayQueue.add(5);
		arrayQueue.add(7);*/
		
		addQueueItem(arrayQueue,n,1);
		addQueueItem(arrayQueue,n,4);
		addQueueItem(arrayQueue,n,7);
		addQueueItem(arrayQueue,n,9);
		addQueueItem(arrayQueue,n,10);
		
		
	}

	private static void addQueueItem(ArrayBlockingQueue<Integer> arrayQueue, int queueLength, int queueitem) {
		
		if(arrayQueue.size() < queueLength){
			System.out.println("Well It enters here");
			arrayQueue.add(queueitem);
		}
		else{
			System.out.println("What error it says it is full ");
			System.out.println("It is remvoed from queue is " +arrayQueue.poll());
			arrayQueue.add(queueitem);
			
		}
	}

}
