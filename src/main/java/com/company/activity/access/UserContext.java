package com.company.activity.access;


import com.company.activity.domain.User;

public class UserContext {
    /** ThreadLocal对象常用于防止对可变的单实例变量或全局变量进行共享。set和get方法为每个使用该变量的线程都保存一份独立的副本，因此，
     * get总是返回当前线程在调用set时设置的最新值。
     * 1. 例如单线程应用程序中，维持一个全局的数据库连接，并在程序启动时初始化这个连接对象，从而避免在调用每个方法时都传递一个
     * Connection对象。由于JDBC的连接对象不一定是线程安全的，因此将JDBC连接对象保存到ThreadLocal中。（该处使用的是ThreadLocal的这个用法）
     * 2. 当某个频繁执行的操作需要一个临时对象，如一个缓冲区，但是又希望避免在每次执行时都重新分配该临时对象。
     * 3. 当把单线程应用移植到多线程环境中时，将共享变量转换为ThreadLocal对象，可以维持线程安全。
     * 4. 避免每次调用方法时都需要传递上下文信息（该处使用的是ThreadLocal的这个用法）。
     */
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

    public static void removeUser() {
        userHolder.remove();
    }
}
