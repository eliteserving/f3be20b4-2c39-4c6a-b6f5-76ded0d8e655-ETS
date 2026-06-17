package com.example.ussdwebview;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;



public class BootReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(
            Context context,
            Intent intent
    ){



        if(Intent.ACTION_BOOT_COMPLETED.equals(
                intent.getAction()
        )){



            startService(
                    context
            );



            Handler handler =
                    new Handler(
                            Looper.getMainLooper()
                    );



            // retry after 1 minute

            handler.postDelayed(
                    new Runnable(){

                        @Override
                        public void run(){


                            startService(
                                    context
                            );


                        }


                    },

                    60 * 1000

            );







            // retry again after 5 minutes

            handler.postDelayed(
                    new Runnable(){

                        @Override
                        public void run(){


                            startService(
                                    context
                            );


                        }


                    },

                    5 * 60 * 1000

            );



        }


    }








    private void startService(
            Context context
    ){



        Intent service =

                new Intent(
                        context,
                        KeepAliveService.class
                );





        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){



            context.startForegroundService(
                    service
            );



        }else{



            context.startService(
                    service
            );


        }


    }



}