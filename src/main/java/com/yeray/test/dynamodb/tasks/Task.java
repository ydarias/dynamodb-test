package com.yeray.test.dynamodb.tasks;

public interface Task {

    public void launch(int operations, int threads);

}
