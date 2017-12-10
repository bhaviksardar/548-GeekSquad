package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.motors.NeveRest40Gearmotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
//import com.github.pmtischler.control.Mecanum;

/**
 * Created by Bhavik Sardar on 9/28/17.
 */

@TeleOp (name = "T548Teleop1617", group = "T548")

public class T548Telep1617Sept extends OpMode {
    //DcMotor lf;
    DcMotor rf;
    DcMotor rr;
    DcMotor lr;
    DcMotor lf;
    DcMotor r1;
    DcMotor l1;
    DcMotor LinearSlide;
    Servo glyphl;
    Servo glyphr;
    Servo color;
    Servo glyphStopper;
    boolean slideMotorOn = false;
    double slideModeTimeStamp;
    boolean slideRetainingMode = false;
    boolean driveFoward = true;
    private void InitializeRobot() {
        rf = hardwareMap.dcMotor.get("RightFront");
        lr = hardwareMap.dcMotor.get("LeftBack");
        rr = hardwareMap.dcMotor.get("RightBack");
        lf = hardwareMap.dcMotor.get("LeftFront");
        r1 = hardwareMap.dcMotor.get("GlyphRight");
        l1 = hardwareMap.dcMotor.get("GlyphLeft");
        glyphStopper = hardwareMap.servo.get("GlyphStopper");
        //LinearSlide = hardwareMap.dcMotor.get("LinearSlide");
        //color = hardwareMap.servo.get("ColorServo");
        rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        final double maxPower = 1;

        // set power to zero to avoid a FTC bug
        rf.setPower(0);
        lf.setPower(0);
        lr.setPower(0);
        rr.setPower(0);
        glyphStopper.setPosition(0);
        //color.setPosition(1);
    }

    public void init() {
        InitializeRobot();
    }


    private double Joy1Y1() {
        return (gamepad1.left_stick_y);
    }

    private double Joy1Y2() {
        return (gamepad1.right_stick_y);
    }

    private double Joy1X1() {

        return (gamepad1.left_stick_x);
    }

    private double Joy1X2() {

        return (gamepad1.right_stick_x);
    }

    private double joy1DLT(){
        return (gamepad1.left_trigger);
    }

    private double joy1DRT(){
        return (gamepad1.right_trigger);
    }

    private boolean joy1DL() {
        return (gamepad1.dpad_left || gamepad2.dpad_left);
    }

    private boolean joy1DR() {
        return (gamepad1.dpad_right || gamepad2.dpad_right);
    }

    private boolean joy1X() {

        return (gamepad1.x || gamepad2.x);
    }


    private boolean joy1Back() {
        return (gamepad1.back || gamepad2.back);
    }

    private boolean joy1Start() {
        return (gamepad1.start || gamepad2.start);
    }

    private boolean glyph(){
        return (gamepad2.a);
    }

    private boolean pickUp(){
        return (gamepad2.a);
    }

    private boolean keepIn(){
        return (gamepad2.b);
    }

    private double straferight() {return (gamepad1.right_trigger);}

    public double scaleDrive(double x) {
        if (x < .1 && x > -.1) {
            // Game pad has a little drift. At resting, it may return +/-0,05, set a dead zone here.
            return 0.0;
        }
        // limit to +/- 1.0 motor power
        if (x > 1) {
            return 1;
        }
        if (x < -1) {
            return -1;
        }
        return x;
    }

    private void drive() {
        double leftPower, rightPower;

        float throttle_right, throttle_left;
        if(joy1DR()) {
            driveFoward = true;
        }
        else if(joy1DL()) {
            driveFoward = false;
        }

        if(driveFoward) {
            leftPower =scaleDrive(gamepad1.left_stick_y);
            rightPower = scaleDrive(gamepad1.left_stick_y);
        }
        else {
            leftPower = scaleDrive(-gamepad1.left_stick_y);
            rightPower = scaleDrive(-gamepad1.left_stick_y);
        }

        //telemetry.addData("sl=", leftPower);
        //telemetry.addData("sr=", rightPower);

        rf.setPower(rightPower);
        rr.setPower(rightPower);
        lf.setPower(leftPower);
        lr.setPower(leftPower);
    }

    public void translate(){
        double r = Math.hypot(gamepad1.right_stick_x, gamepad1.right_stick_y);
        double robotAngle = Math.atan2(gamepad1.right_stick_x, gamepad1.right_stick_y) - Math.PI / 4;
        double rightX = gamepad1.right_stick_x;
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        lf.setPower(v1);
        rf.setPower(v2);
        lr.setPower(v3);
        rr.setPower(v4);
    }

    public void Mechanum(){
        final double maxPower = 1;

        double joy1Y = -gamepad1.left_stick_x;
        joy1Y = Math.abs(joy1Y) > 0.15 ? joy1Y*3/4: 0;
        double joy1X = gamepad1.left_stick_y;
        joy1X = Math.abs(joy1X) > 0.15 ? joy1X*3/4: 0;
        double joy2X = gamepad1.right_stick_x;
        joy2X = Math.abs(joy2X) > 0.15 ? joy2X*3/4: 0;

        rf.setPower(Math.max(-maxPower, Math.min(maxPower, joy1Y + joy2X + joy1X)));
        lf.setPower(Math.max(-maxPower, Math.min(maxPower, joy1Y + joy2X - joy1X)));
        rr.setPower(Math.max(-maxPower, Math.min(maxPower, joy1Y - joy2X - joy1X)));
        lr.setPower(Math.max(-maxPower, Math.min(maxPower, joy1Y - joy2X + joy1X)));
    }
    public void glyphHold(){
        if (glyph()){
            glyphl.setPosition(0.6);
            glyphr.setPosition(0.2);
        }

        else {
            glyphl.setPosition(0.05);
            glyphr.setPosition(0.9);
        }

    }

    public void pickUpGlyph(){
        if(pickUp()){
            r1.setPower(0.5);
            l1.setDirection(DcMotor.Direction.REVERSE);
            l1.setPower(0.5);
        }
    }

    public void glyphStable(){
        if(keepIn()){
            r1.setPower(0);
            l1.setPower(0);
        }
    }

    public void servoOff(){
        if(gamepad2.right_trigger == 1){
            glyphStopper.setPosition(0.9);
        }
        else {
            glyphStopper.setPosition(0);
        }
    }


    private void runLinearSlide() {
        if (gamepad2.left_trigger == 1.0) {
            LinearSlide.setPower(-1);
            //slideRetainingMode = true;  // enter slide retaining mode
            slideMotorOn = false;
            slideModeTimeStamp = System.currentTimeMillis();
            //  telemetry.addData("Direction", "= up");
        } else if (gamepad2.right_trigger == 1.0) {
            LinearSlide.setPower(.1);
        }
        else {
            LinearSlide.setPower(0);
        }

        if (gamepad2.right_trigger == 1.0) {
            LinearSlide.setPower(.3);
            //slideRetainingMode = false;  // exit slide retaining mode
            //   telemetry.addData("Direction" , "= false");
        } else {
            // No slide button pressed, do slide retaining if needed
            if (slideRetainingMode) {
                // In slide retaining mode
                if (!slideMotorOn && (System.currentTimeMillis() - slideModeTimeStamp > 1000)) {
                    // Slide retaining mode, motor off too long, turn on the motor
                    slideMotorOn = true;
                    slideModeTimeStamp = System.currentTimeMillis();
                } else if (slideMotorOn && System.currentTimeMillis() - slideModeTimeStamp > 100) {
                    // Slide retaining mode, motor on too long, turn off the motor
                    slideMotorOn = false;
                    slideModeTimeStamp = System.currentTimeMillis();
                }
                if (!slideMotorOn) {
                    LinearSlide.setPower(0);
                } else {
                    LinearSlide.setPower(-1);
                }
            } else {
                // Not in slide retention mode, motor off
                LinearSlide.setPower(0);
            }
        }

    }

    //Main control function for Teleop
    public void loop() {
        Mechanum();
        pickUpGlyph();
        glyphStable();
        servoOff();
    }


}


