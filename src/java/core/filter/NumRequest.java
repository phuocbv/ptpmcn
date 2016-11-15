/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.filter;

/**
 *
 * @author dacuoi
 */
public class NumRequest extends Thread {

    protected static int request_number = 0;

    public void run() {

        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {

            }
            request_number = 0;
        }
    }

    public static void add_request() {
        ++request_number;
        System.out.println(request_number);
    }
}
