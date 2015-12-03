package com.example.administrator.rocking.fragment;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.rocking.MainActivity;
import com.example.administrator.rocking.R;
import com.example.administrator.rocking.connect.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ContentFragmentIndex extends Fragment implements View.OnClickListener {

    final int HEIGHT = 320;   //设置画图范围高度
    final int WIDTH = 320;    //画图范围宽度
    final int X_OFFSET = 5;  //x轴（原点）起始位置偏移画图范围一点
    private int cx = X_OFFSET;  //实时x的坐标
    int centerY = HEIGHT / 2;  //y轴的位置
    TextView myview = null;   //画布下方显示获取数据的地方
    final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //uuid 此为单片机蓝牙模块用
    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //获取本手机的蓝牙适配器
    static int REQUEST_ENABLE_BT = 1;  //开启蓝牙时使用
    BluetoothSocket socket = null;    //用于数据传输的socket
    int READ = 1;                   //用于传输数据消息队列的识别字
    int OPEN = 2;
    int CLOSE = 3;

    int paintflag = 1;//绘图是否暂停标志位，0为暂停
    boolean controlFlg = false;
    boolean controlFlg1 = false;
    public ConnectedThread thread = null;   //连接蓝牙设备线程
    static int temp = 0;                //临时变量用于保存接收到的数据
    private SurfaceHolder holder = null;    //画图使用，可以控制一个SurfaceView
    private Paint paint = null;      //画笔
    SurfaceView surface = null;     //
    Timer timer = new Timer();       //一个时间控制的对象，用于控制实时的x的坐标，
    //使其递增，类似于示波器从前到后扫描
    TimerTask task = null;   //时间控制对象的一个任务
    private Button stop_bn = null;//暂停按钮
    private Button control_bn = null;//开关按钮

    private InputStream mmInStream;
    private OutputStream mmOutStream;

    private List<String> listDevices = new ArrayList<String>();
    private ArrayAdapter<String> adtDevices;//显示搜索到的设备信息
    BlueBroadcastReceiver mReceiver = new BlueBroadcastReceiver();

    /* 关于画图类的几点说明
     * SurfaceView 是View的继承类，这个视图里
     * 内嵌了一个专门用于绘制的Surface。可以控制这个Surface的格式和尺寸。
     * SurfaceView控制这个Surface的绘制位置。
     *
     * 实现过程：继承SurfaceView并实现SurfaceHolder.Callback接口------>
     * SurfaceView.getHolder()获得SurfaceHolder对象----->SurfaceHolder.addCallback(callback)
     * 添加回调函数----->surfaceHolder.lockCanvas()获得Canvas对象并锁定画布------>
     * Canvas绘画------->SurfaceHolder.unlockCanvasAndPost(Canvas canvas)结束锁定画图，
     * 并提交改变，将图形显示。
     *
     * 这里用到了一个类SurfaceHolder，可以把它当成surface的控制器，
     * 用来操纵surface。处理它的Canvas上画的效果和动画，控制表面，大小，像素等
     *
     * 其中有几个常用的方法，锁定画布，结束锁定画布
     * */
    private Button contro2_bn = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_content_index, container, false);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("首页");
        view.findViewById(R.id.iv_title).setOnClickListener(this);

        myview = (TextView)view.findViewById(R.id.myview);
        Button bluetooth =(Button)view.findViewById(R.id.button);
        // Button clear =(Button)findViewById(R.id.clear);
        stop_bn=(Button)view.findViewById(R.id.stop_bn);
        control_bn=(Button)view.findViewById(R.id.controlBtn);
        contro2_bn = (Button) view.findViewById(R.id.controlBtn1);
        stop_bn.setOnClickListener(new MyButtonStopListener());
        surface = (SurfaceView)view.findViewById(R.id.show);
        //初始化SurfaceHolder对象
        holder = surface.getHolder();
        holder.setFixedSize(WIDTH+50, HEIGHT+100);  //设置画布大小，要比实际的绘图位置大一点
        /*设置波形的颜色等参数*/
        paint = new Paint();
        paint.setColor(Color.GREEN);  //画波形的颜色是绿色的，区别于坐标轴黑色
        paint.setStrokeWidth(3);
        bluetooth.setOnClickListener(new MyButtonListener());
        //添加按钮监听器   开启蓝牙 开启连接通信线程
        // clear.setOnClickListener(new MyButtonClearListener());

        holder.addCallback(new Callback() {
            public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
                drawBack(holder);
                //如果没有这句话，会使得在开始运行程序，整个屏幕没有白色的画布出现
                //直到按下按键，因为在按键中有对drawBack(SurfaceHolder holder)的调用
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }
        });
        //添加按钮监听器  清除TextView内容
        adtDevices=new ArrayAdapter<String>(getActivity(),R.layout.array_item,listDevices);
        // Register the BroadcastReceiver
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, intent); // Don't forget to unregister during onDestroy



        control_bn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                handler.obtainMessage(controlFlg ? CLOSE : OPEN).sendToTarget();     //压入消息队列
                control_bn.setText(controlFlg ? "开" : "关");
                controlFlg = !controlFlg;
            }
        });

        contro2_bn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String re = "";
                if (controlFlg1) {
                    re = "G1";

                } else {
                    re = "K1";
                }
                sendCMD(re);
                contro2_bn.setText(controlFlg1 ? "开摇椅" : "关摇椅");
                controlFlg1 = !controlFlg1;
            }
        });
        return view;
    }
    public void sendCMD(String cmd){

//	 cmd =cmd+"\r\n";
        try {
            mmOutStream.write(cmd.getBytes());
            mmOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title:
                ((MainActivity) getActivity()).openMenu();
                break;
        }
    }


    class BlueBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            // When discovery finds a device
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                String str=(device.getName() + "|" + device.getAddress());
                listDevices.add(str);
                adtDevices.notifyDataSetChanged();//动态更新listview
            }


        }

    }
    /*蓝牙启动按钮*/
    class MyButtonListener implements View.OnClickListener {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            //如果没有打开蓝牙，此时打开蓝牙


            String address = "98:D3:31:50:3B:FC";

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            Method m;            //建立连接
            try {
                m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
            } catch (SecurityException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                //socket = device.createRfcommSocketToServiceRecord(uuid); //建立连接（该方法不能用)
                mBluetoothAdapter.cancelDiscovery();
                //取消搜索蓝牙设备
                socket.connect();
                setTitle("连接成功");

                //Toast.makeText(ContentFragmentIndex.this.getActivity(), "连接成功", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                setTitle("连接失败");//目前连接若失败会导致程序出现ANR
            }
            thread = new ConnectedThread(socket);  //开启通信的线程
            thread.start();
        }

    }

    public void setTitle(String content){
        Toast.makeText(getActivity(),content,Toast.LENGTH_LONG).show();
    }
    class MyButtonStopListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (paintflag == 1) {
                paintflag = 0;
                stop_bn.setText("开始");
            } else {
                paintflag = 1;
                stop_bn.setText("暂停");
            }

        }

    }

    class MyButtonClearListener implements View.OnClickListener {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            myview.setText("");
        }

    }

    connect co = new connect(getActivity());
    Handler handler = new Handler() {  //这是处理消息队列的Handler对象

        @Override
        public void handleMessage(Message msg) {
            //处理消息
            if (msg.what == READ) {
                final String str = (String) msg.obj;    //类型转化
                //myview.append(" "+str);	  //显示在画布下方的TextView中
                myview.setText(str);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        co.con(str);

                    }
                }).start();
                //co.con(str);
                try {
                    temp = Integer.parseInt(str);
                } catch (Exception e) {
                    // TODO: handle exception

                }

                
            } else if (msg.what == OPEN) {
                try {
                    if (mmOutStream != null) {
                        mmOutStream.write("K".getBytes());
                        mmOutStream.flush();
                        Toast.makeText(ContentFragmentIndex.this.getActivity(), "开关已打开", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (msg.what == CLOSE) {
                try {
                    if (mmOutStream != null) {
                        mmOutStream.write("G".getBytes());
                        mmOutStream.flush();
                        Toast.makeText(ContentFragmentIndex.this.getActivity(), "开关已关闭", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            super.handleMessage(msg);
        }

    };

    /*
     * 该类只实现了数据的接收，蓝牙数据的发送自行实现
     *
     * */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;

        //构造函数
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream(); //获取输入流
                tmpOut = socket.getOutputStream();  //获取输出流
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            BufferedReader br = new BufferedReader(new InputStreamReader(mmInStream));

            while (true) {
                try {
                    // Read from the InputStream

                    // bytes = mmInStream.read(buffer); //bytes数组返回值，为buffer数组的长度
                    // Send the obtained bytes to the UI activity
                    // String str = new String(buffer);

                    final String str = br.readLine();
                    temp = byteToInt(buffer);   //用一个函数实现类型转化，从byte到int
                    System.out.println(temp);
                    //Toast.makeText(ContentFragmentIndex.this.getActivity(), temp, Toast.LENGTH_SHORT).show();

                    handler.obtainMessage(READ, str.length(), -1, str)
                            .sendToTarget();     //压入消息队列


                } catch (Exception e) {
                    System.out.print("read error");
                    break;

                }
            }
        }
    }

    //绘图线程，实时获取temp 数值即是y值
    public class DrawThread extends Thread {

        public void run() {
            // TODO Auto-generated method stub
            drawBack(holder);    //画出背景和坐标轴
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() { //新建任务


                @Override
                public void run() {
                    if (paintflag == 1) {
                        //获取每一次实时的y坐标值
                        //以下绘制的是正弦波，若需要绘制接收到的数据请注释掉下面的cy[];
                        int cy = centerY + temp; //实时获取的temp数值，因为对于画布来说

                        //int cy = centerY + temp; //实时获取的temp数值，因为对于画布来说

                        //最左上角是原点，所以我要到y值，需要从画布中间开始计数

                        Canvas canvas = holder.lockCanvas(new Rect(cx, cy - 2, cx + 2, cy + 2));

                        paint.setColor(Color.RED);//设置波形颜色
                        canvas.drawPoint(cx, cy, paint); //打点

                        holder.unlockCanvasAndPost(canvas);  //解锁画布

                        cx++;    //cx 自增， 就类似于随时间轴的图形
                        cx++; //间距自己设定
                        if (cx >= WIDTH) {
                            cx = 5;     //如果画满则从头开始画
                            drawBack(holder);  //画满之后，清除原来的图像，从新开始
                        }

                    }
                }
            };
            timer.schedule(task, 0, 1); //隔1ms被执行一次该循环任务画出图形
            //简单一点就是1ms画出一个点，然后依次下去

        }
    }

    //设置画布背景色，设置XY轴的位置
    private void drawBack(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas(); //锁定画布
        //绘制白色背景
        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStrokeWidth(2);

        //绘制坐标轴
        canvas.drawLine(X_OFFSET, centerY, WIDTH, centerY, p); //绘制X轴 前四个参数是起始坐标
        canvas.drawLine(X_OFFSET, 20, X_OFFSET, HEIGHT, p); //绘制Y轴 前四个参数是起始坐标

        holder.unlockCanvasAndPost(canvas);  //结束锁定 显示在屏幕上
        holder.lockCanvas(new Rect(0, 0, 0, 0)); //锁定局部区域，其余地方不做改变
        holder.unlockCanvasAndPost(canvas);

    }

    //数据转化，从byte到int
    /*
     * 其中 1byte=8bit，int = 4 byte，
     * 一般单片机比如c51 8位的  MSP430  16位 所以我只需要用到后两个byte就ok
     * */
    public static int byteToInt(byte[] b) {
        return (((int) b[0]) + ((int) b[1]) * 256);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());

        thread.destroy();
    }

}
