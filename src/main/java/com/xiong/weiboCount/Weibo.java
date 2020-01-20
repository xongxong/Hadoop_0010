package com.xiong.weiboCount;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Weibo implements WritableComparable<Object> {
    //粉丝
    private int fan;
    //关注
    private int follower;
    //微博数
    private int microblogs;

    public Weibo() {

    }

    public Weibo(int fan, int follower, int microblogs) {
        this.fan = fan;
        this.follower = follower;
        this.microblogs = microblogs;
    }

    public void setWeibo(int fan, int follower, int microblogs) {
        this.fan = fan;
        this.follower = follower;
        this.microblogs = microblogs;
    }

    public int getFan() {
        return fan;
    }

    public int getFollower() {
        return follower;
    }

    public int getMicroblogs() {
        return microblogs;
    }

    public void setFan(int fan) {
        this.fan = fan;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public void setMicroblogs(int microblogs) {
        this.microblogs = microblogs;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    //实现writableComparable的write()方法，以便该数据能被序列化后完成网络传输或文件传输
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(fan);
        dataOutput.writeInt(follower);
        dataOutput.writeInt(microblogs);
    }

    //实现writableComparable的readFields()方法，以便该数据能序列化后完成网络传输或文件输入
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        fan = dataInput.readInt();
        follower = dataInput.readInt();
        microblogs = dataInput.readInt();
    }
}
