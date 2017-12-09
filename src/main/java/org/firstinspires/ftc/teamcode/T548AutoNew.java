package org.firstinspires.ftc.teamcode;

//import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;


/**
 * Team 548 Geek Squad Autonomous
 */

@Autonomous(name = "T548AutoNew", group = "T548")

public class T548AutoNew extends LinearOpMode {

    // Concersion for encoder to inch
    static final double COUNTS_PER_MOTOR_REV = 1440;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = (2.0 / 3.0);     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    Servo Color;
    ColorSensor jsensor;
    DcMotor rf;
    DcMotor rr;
    DcMotor lr;
    DcMotor lf;
    Servo color;
    Servo jewel;
    BNO055IMU imu;
    private boolean blueTeam = false;
    private boolean leftSide= false;
    private int delayStartTime = 0;
    private ElapsedTime runtime = new ElapsedTime();


    // Basic function to implement sleep. Give value in millisecond.
    // The loop will check of opModeIsActive() to allow forced termination of the loop.
    private void RobotSleep(double timeInMillisecond) {
        double timeOutS = timeInMillisecond / 1000.0;
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < timeOutS)) {
            // do nothing
        }
    }

    // Read the game pad to set the team color and delay start time
    private void SelectTeamColor() throws InterruptedException {
        //This while loop runs forever until user presses start to break out of loop
        while (true) {
            //Pressing start breaks out of loop
            if (gamepad1.start) {
                break;
            }
            //If User 1 presses B, then the team selected is red
            if (gamepad1.b) {
                blueTeam = false;
                RobotSleep(200);
            }
            //If User 1 presses X, then the team selected is blue
            if (gamepad1.x) {
                blueTeam = true;
                RobotSleep(200);
            }
            //If User 1 presses Y, then the delay time is increased by 1 second
            if (gamepad1.y) {
                delayStartTime++;
                RobotSleep(200);
            }
            //If User 1 presses A, then the delay time is decreased by 1 second
            if (gamepad1.a && (delayStartTime > 0)) {
                delayStartTime--;
                RobotSleep(200);
            }
            telemetry.addData("blueTeam ", blueTeam);
            telemetry.addData("delayStart ", delayStartTime);
            telemetry.update();
        }
        telemetry.addData("Selected blueTeam ", blueTeam);
        telemetry.addData("Selected delayStart ", delayStartTime);
        telemetry.update();
    }

    private void SelectTeamLocationAndColor() throws InterruptedException {
        //This while loop runs forever until user presses start to break out of loop
        while (true) {
            //Pressing start breaks out of loop
            if (gamepad1.start) {
                break;
            }
            if (gamepad1.b) {
                blueTeam = false;
                RobotSleep(200);
            }
            //If User 1 presses X, then the team selected is blue
            if (gamepad1.x) {
                blueTeam = true;
                RobotSleep(200);
            }
            //If User 1 presses dpad_left, then the team selected is left
            if(gamepad1.dpad_left){
                leftSide=true;
                RobotSleep(200);
            }
            //If User 1 presses dpad_right, then the team selected is right
            if(gamepad1.dpad_right){
                leftSide=false;
                RobotSleep(200);
            }

            telemetry.addData("blueTeam ", blueTeam);
            telemetry.addData("leftSide ", leftSide);
        }
        telemetry.addData("Selected blueTeam ", blueTeam);
        telemetry.addData("leftSide ", leftSide);
        //telemetry.addData("Selected delayStart ", delayStartTime);
        telemetry.update();
    }

    //This function takes the delay time and sleeps the robot for any amount of seconds
    private void DelayTime(int delayTimeInSec) throws InterruptedException {
        RobotSleep(delayTimeInSec * 1000);
    }

    // Timed drive function for forward, backward, left and right
    private void DriveByTime(double leftPower, double rightPower, int driveTime) throws InterruptedException {
        rf.setPower(rightPower);
        lf.setPower(leftPower);
        rr.setPower(rightPower);
        lr.setPower(leftPower);
        RobotSleep(driveTime);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setPower(0);
        lf.setPower(0);
        rr.setPower(0);
        lr.setPower(0);
    }

    private void DriveByTimeCoast(double leftPower, double rightPower, int driveTime) throws InterruptedException {
        rf.setPower(rightPower);
        //lf.setPower(leftPower);
        rr.setPower(rightPower);
        lr.setPower(leftPower);
        RobotSleep(driveTime);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        //lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rf.setPower(0);
        //lf.setPower(0);
        rr.setPower(0);
        lr.setPower(0);
    }

    private void RunByEncoderToPosition(double leftPower, double rightPower,
                                        double leftInches, double rightInches, double timeOutMillisecond, boolean coast) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            double timeOutS = timeOutMillisecond / 1000.0;

            // Determine new target position, and pass to motor controller
            newLeftTarget = lf.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = rf.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            lf.setTargetPosition(newLeftTarget);
            lr.setTargetPosition(newLeftTarget);
            rf.setTargetPosition(newRightTarget);
            rr.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            lf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            lr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rf.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rr.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            //Left Percent Change
            lf.setPower(leftPower);
            lr.setPower(leftPower);
            rf.setPower(rightPower);
            rr.setPower(rightPower);

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeOutS) &&
                    (lf.isBusy() && rf.isBusy()) && lr.isBusy() && rr.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        lf.getCurrentPosition(),
                        lr.getCurrentPosition(),
                        rf.getCurrentPosition(),
                        rr.getCurrentPosition()
                );
                telemetry.update();
            }

            // Stop all motion;
            if (coast) {
                rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            } else {
                rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            lf.setPower(0);
            lr.setPower(0);
            rf.setPower(0);
            rr.setPower(0);

            // Turn off RUN_TO_POSITION
            lf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            lr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rf.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    private void DriveByEncoderDiffWheel(double leftPower, double rightPower,
                                         double leftInches, double rightInches, double timeOutMillisecond, boolean coast) {
        if ((Math.abs(leftInches) < 10) || (Math.abs(rightInches) < 10)) {
            // Short distance use the RunToPosition mode is more accurate
            RunByEncoderToPosition(leftPower, rightPower, leftInches, rightInches, timeOutMillisecond, coast);
            return;
        }

        if (leftInches < 0) {
            leftPower = leftPower * (-1);
        }
        if (rightInches < 0) {
            rightPower = rightPower * (-1);
        }

        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            double timeOutS = timeOutMillisecond / 1000.0;
            // reset the timeout time and start motion.
            runtime.reset();

            // Determine new target position, and pass to motor controller
            int leftTarget = (int) (leftInches * COUNTS_PER_INCH);
            int rightTarget = (int) (rightInches * COUNTS_PER_INCH);
            int targetDistance = Math.abs(leftTarget) + Math.abs(rightTarget);
            double leftStartVal = lf.getCurrentPosition();
            double leftStartVal2=lr.getCurrentPosition();
            double rightStartVal = rf.getCurrentPosition();
            double rightStartVal2 = rr.getCurrentPosition();

            lf.setPower(leftPower);
            lr.setPower(leftPower);
            rf.setPower(rightPower);
            rr.setPower(rightPower);

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() && (runtime.seconds() < timeOutS)) {
                double leftDistance = lf.getCurrentPosition() - leftStartVal;
                double rightDistance = rf.getCurrentPosition() - rightStartVal;
                double sumDistance = Math.abs(leftDistance) + Math.abs(rightDistance);
                if (targetDistance <= sumDistance) {
                    break;
                }

            }

            // Stop all motion;
            if (coast) {
                rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            } else {
                rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            lf.setPower(0);
            lr.setPower(0);
            rf.setPower(0);
            rr.setPower(0);
        }
    }

    private void DriveByEncoder(double speed, double leftDistance, double rightDistance, double timeoutMilliSecond) {
        DriveByEncoderDiffWheel(speed, speed, leftDistance, rightDistance, timeoutMilliSecond, false);
    }

    public void DriveForwardByTime(double power, int wait) throws InterruptedException {
        DriveByTime(power, power, wait);
    }

    public void DriveForwardByTimeCoast(double power, int wait) throws InterruptedException {
        DriveByTimeCoast(power, power, wait);
    }

    private void DriveForwardByEncoder(double speed, double inches, double timeoutMilliSecond) {
        DriveByEncoder(speed, inches, inches, timeoutMilliSecond);
    }

    private void DriveBackwardByEncoder(double speed, double inches, double timeoutMilliSecond) {
        DriveByEncoder(speed, -inches, -inches, timeoutMilliSecond);
    }

    public void DriveBackwardByTimeCoast(double power, int wait) throws InterruptedException {
        DriveByTimeCoast(power, power, wait);
    }

    public void DriveBackwardByTime(double power, int howLong) throws InterruptedException {
        DriveByTime(-power, -power, howLong);
    }

    // Left wheels backward, right wheels forward to turn left
    public void TurnLeftByTime(double power, int howLong) throws InterruptedException {
        DriveByTime(-power, power, howLong);
    }

    // Left wheels forward, right wheels backward to turn right
    public void TurnRightByTime(double power, int howLong) throws InterruptedException {
        DriveByTime(power, -power, howLong);
    }



    private void DriveByTimeDiffWheel(double rightPower, double leftPower, int driveTime) {
        rf.setPower(rightPower);
        lf.setPower(leftPower);
        rr.setPower(rightPower);
        lr.setPower(leftPower);
        RobotSleep(driveTime);
        rf.setPower(0);
        lf.setPower(0);
        rr.setPower(0);
        lr.setPower(0);
    }

    /*private void TurnToGyroHeading(double leftSpeed, double rightSpeed, int targetHeading, double timeOutMillisecond) throws InterruptedException {
        boolean turningRight = (leftSpeed > rightSpeed);
        int currentHeading;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            // reset the timeout time and start motion.
            runtime.reset();
            // turn on motors
            lf.setPower(leftSpeed);
            rf.setPower(rightSpeed);
            lr.setPower(leftSpeed);
            rr.setPower(rightSpeed);
            // keep looping while we are still active, and there is time left, and both motors are running.
            double timeOutS = timeOutMillisecond / 1000.0;
            while (opModeIsActive() && (runtime.seconds() < timeOutS)) {
                currentHeading = imu.getIntegratedZValue();
                if (turningRight) {  // gyro value decreasing turn right is clockwise
                    if (currentHeading <= targetHeading)
                        break;
                } else {
                    if (currentHeading >= targetHeading)
                        break;
                }
                //DbgLog.msg("T548 TurnToGyroHeading cH = " + currentHeading);
                // Display it for debug.
                //telemetry.addData("currentHeading = ", currentHeading);
                //telemetry.update();
            }
            // Stop all motion;
            rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            lf.setPower(0);
            rf.setPower(0);
            lr.setPower(0);
            rr.setPower(0);
            //DbgLog.msg("T548 TurnToGyroHeading Motor Stop gyro = " + gyro.getIntegratedZValue());
        }
    }
    // Turn left or right by gyro sensor given the turnAngle in degree
    private void TurnByGyro(double leftSpeed, double rightSpeed, int turnAngle, double timeOutMillisecond) throws InterruptedException {
        boolean turningRight = (leftSpeed > rightSpeed);
        int currentHeading = g.getIntegratedZValue();
        int targetHeading;
        if (turningRight) {
            // Right turn decreases gyro angle
            targetHeading = currentHeading - turnAngle;
        } else {
            targetHeading = currentHeading + turnAngle;
        }
        TurnToGyroHeading(leftSpeed, rightSpeed, targetHeading, timeOutMillisecond);
/*
        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            // reset the timeout time and start motion.
            runtime.reset();
            // turn on motors
            leftWheel.setPower(leftSpeed);
            rightWheel.setPower(rightSpeed);
            double timeOutS = timeOutMillisecond / 1000.0;
            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() && (runtime.seconds() < timeOutS)) {
                currentHeading = gyro.getIntegratedZValue();
                if (turningRight) {
                    // When turning right, gyro value is decreasing. Turn right is clockwise when robot is viewed from the top
                    if (currentHeading <= targetHeading)
                        break;
                } else {
                    if (currentHeading >= targetHeading)
                        break;
                }
                // Display it for debug.
                telemetry.addData("currentHeading = ", currentHeading);
                telemetry.update();
            }
            // Stop all motion;
            rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            leftWheel.setPower(0);
            rightWheel.setPower(0);
        }
    }
    // Turn left by gyro sensor given the turnAngle in degree
    private void TurnLeftByGyro(double speed, int turnAngle, double timeOutMillisecond) throws InterruptedException {
        TurnByGyro(-speed, speed, turnAngle, timeOutMillisecond);
    }
    // Turn right by gyro sensor given the turnAngle in degree
    private void TurnRightByGyro(double speed, int turnAngle, double timeOutMillisecond) throws InterruptedException {
        TurnByGyro(speed, -speed, turnAngle, timeOutMillisecond);
    }
    // Turn left by gyro sensor given the turnAngle in degree. Left right wheel power is different.
    private void TurnLeftByGyroDiffWheel(double leftSpeed, double rightSpeed, int turnAngle, double timeOutMillisecond) throws InterruptedException {
        TurnByGyro(-leftSpeed, rightSpeed, turnAngle, timeOutMillisecond);
    }
    // Turn right by gyro sensor given the turnAngle in degree. Left right wheel power is different.
    private void TurnRightByGyroDiffWheel(double leftSpeed, double rightSpeed, int turnAngle, double timeOutMillisecond) throws InterruptedException {
        TurnByGyro(leftSpeed, -rightSpeed, turnAngle, timeOutMillisecond);
    }
    // Turn to heading where left right wheel power are controllable
    private void TurnleftToGyroHeadingDiffWheel(double leftSpeed, double rightSpeed, int targetHeading, double timeOutMillisecond) throws InterruptedException {
        TurnToGyroHeading(-leftSpeed, rightSpeed, targetHeading, timeOutMillisecond);
    }
    // Turn to heding where left right wheel power are controllable
    private void TurnRightToGyroHeadingDiffWheel(double leftSpeed, double rightSpeed, int targetHeading, double timeOutMillisecond) throws InterruptedException {
        TurnToGyroHeading(leftSpeed, -rightSpeed, targetHeading, timeOutMillisecond);
    }
    // Turn left to an absolute gyro heading
    private void TurnLeftToGyroHeading(double speed, int targetHeading, double timeOutMillisecond) throws InterruptedException {
        TurnToGyroHeading(-speed, speed, targetHeading, timeOutMillisecond);
    }
    // Turn right to an absolute gyro heading
    private void TurnRightToGyroHeading(double speed, int targetHeading, double timeOutMillisecond) throws InterruptedException {
        TurnToGyroHeading(speed, -speed, targetHeading, timeOutMillisecond);
    }
    //turn to specific heading, left or right
    private void TurnLeftOrRightToGyroHeading(double speed, int targetHeading, double timeOutMillisecond) throws InterruptedException {
        int currentHeading = g.getIntegratedZValue();
     /*   if (Math.abs(currentHeading - targetHeading) <= 2) {
            // Don't turn if we are already within 2 degree of the target heading
            return;
        }*/

    // Clockwise turn decrease gyro value
       /* if (currentHeading < targetHeading) {
            // Target is to the left, need counter clockwise turn
            TurnLeftToGyroHeading(speed, targetHeading, timeOutMillisecond);
        } else {
            TurnRightToGyroHeading(speed, targetHeading, timeOutMillisecond);
        }
    }
    // Basic function to provide driving by wheel encoder
/*
    private void DriveByEncoder_NoLongerNeeded(double speed,
                                               double leftInches, double rightInches,
                                               double timeOutMillisecond) {
        int newLeftTarget;
        int newRightTarget;
        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            double timeOutS = timeOutMillisecond / 1000.0;
            // Determine new target position, and pass to motor controller
            newLeftTarget = leftWheel.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = rightWheel.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            leftWheel.setTargetPosition(newLeftTarget);
            rightWheel.setTargetPosition(newRightTarget);
            // Turn On RUN_TO_POSITION
            leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // reset the timeout time and start motion.
            runtime.reset();
            leftWheel.setPower(Math.abs(speed));
            rightWheel.setPower(Math.abs(speed));
            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeOutS) &&
                    (leftWheel.isBusy() && rightWheel.isBusy())) {
                // Display it for the driver.
                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d",
                        leftWheel.getCurrentPosition(),
                        rightWheel.getCurrentPosition());
                telemetry.update();
            }
            // Stop all motion;
            rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            leftWheel.setPower(0);
            rightWheel.setPower(0);
            // Turn off RUN_TO_POSITION
            leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
*/




    // Sense the beacon color
    //private boolean beaconColorIsRed() {
    //    return ((beaconSensor.red() > 1) && (beaconSensor.blue() < 5));
    //}


    int BEACON_BLUE = 1;
    int BEACON_RED = (-1);
    int BEACON_UNDECIDED = 0;

    // Sense the beacon color
    private int ReadBeaconColor() {
        int blueValue = jsensor.blue();
        int redValue = jsensor.red();
        int greenValue = jsensor.green();
        //DbgLog.msg("T548 ReadBeaconColor r=" + redValue + " b=" + blueValue + " g=" + greenValue);
        if ((blueValue >= 3) && (redValue <= 1))
            return (BEACON_BLUE);
        if ((redValue >= 2) && (blueValue <= 5))
            return (BEACON_RED);
        if ((redValue <= 0) && (blueValue >= 2))
            return (BEACON_BLUE);
        if ((blueValue <= 0) && (redValue >= 1))
            return (BEACON_RED);
        return (BEACON_UNDECIDED);
    }

    // Sense the white line on th floor


    // Drive forward or backward guided by the gyro direction given by targetHeading. Stop after the runningTime.
    // Positive power drive forward. Negative power drive backward.


    // Drive backward is drive forward with negative power
    private void DriveBackwardByTimeGyro(double power, int runningTime, int targetHeading) throws InterruptedException {
        DriveBackwardByTimeGyro(-power, runningTime, targetHeading);
    }
    /*
    private void DriveForwardByTimeGyroUltrasonic(double power, int runningTime, int targetHeading, double idealDistance) throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        while (opModeIsActive() && ((currentTime + runningTime) > System.currentTimeMillis())) {
            double rightPower = power;
            double leftPower = power;
            // When turning right, gyro value is decreasing. Turn right is clockwise when robot is viewed from the top
            int headingDiff = gyro.getIntegratedZValue() - targetHeading;
            if (headingDiff > 10)
                headingDiff = 10;
            if (headingDiff < (-10))
                headingDiff = (-10);
            double powerIncrease = 0;
            // Gets distance from wall via ultrasonic sensor
            double ultraSonicValue = ultrasonic.getUltrasonicLevel();
            double distanceFromWall = ultraSonicValue;
            // Sometimes ultrasound sensor goes wild and gives value of 255
            if (ultraSonicValue >= 60)
                distanceFromWall = idealDistance;
            if (headingDiff != 0) {
                // 10 degree clockwise pointing to the wall, subtract 5cm from ultrasonic reading
                distanceFromWall = distanceFromWall - (Math.abs(headingDiff) / 2.5);
            }
            // Calculates the difference between the ideal distance and the actual distance
            double distanceDiff = distanceFromWall - idealDistance;
            if (distanceDiff > 20)
                distanceDiff = 20;
            if (distanceDiff < (-20))
                distanceDiff = (-20);
            //powerIncrease += (distanceDiff / 20.0);
            //if (powerIncrease > 0.3)
            //        powerIncrease = 0.3; // limit power correction to avoid oscillation
            if (Math.abs(distanceDiff) >= 2) {
                // Maintaining distance to wall is more important. When distance is far from ideal,
                // we only do correction based on distance. Heading is ignored here.
                powerIncrease = distanceDiff / 10.0;
                if (powerIncrease > 0.6) {
                    powerIncrease = 0.6;
                }
                if (powerIncrease < (-0.6)) {
                    powerIncrease = (-0.6);
                }
                if (distanceDiff > 0) {
                    // too far from the wall
                    leftPower = power * (1.0 + Math.abs(powerIncrease));
                    rightPower = power * (1.0 - Math.abs(powerIncrease));
                }
                if (distanceDiff < 0) {
                    // too close to the wall
                    leftPower = power * (1.0 - Math.abs(powerIncrease));
                    rightPower = power * (1.0 + Math.abs(powerIncrease));
                }
            } else {
                powerIncrease = Math.abs(headingDiff / 10.0); // One degree off increase the power by 10%
                //powerIncrease += (distanceDiff / 10.0);
                if (((headingDiff >= 0) && (power > 0)) ||
                        ((headingDiff < 0) && (power < 0))) {                //Log.i("GyroTesting", "heading > 0 ");
                    // Veered towards counter clockwise when moving forward OR
                    // veered towards clockwise when moving backward
                    // Need more power on left wheel, less power on right wheel
                    leftPower = power * (1.0 + powerIncrease);
                    rightPower = power * (1.0 - powerIncrease);
                }
                if (((headingDiff < 0) && (power > 0)) ||
                        ((headingDiff >= 0) && (power < 0))) {                // Veered towards clockwise when moving forward OR
                    // veered towards counter clockwise when moving backward
                    // Need more power on right wheel, less power on left wheel
                    rightPower = power * (1.0 + powerIncrease);
                    leftPower = power * (1.0 - powerIncrease);
                }
            }
            DbgLog.msg("T548 usv= " + ultraSonicValue + " dfw= " + distanceFromWall + " dd=" + distanceDiff + " hd= " + headingDiff);
            DbgLog.msg("T548 pi= " + powerIncrease + " lp= " + leftPower + " rp=" + rightPower);
            //telemetry.addData("distance from wall ", distanceFromWall);
            //telemetry.addData("distance diff ", distanceDiff);
            //telemetry.addData("heading diff ", headingDiff);
            //telemetry.addData("power increase ", powerIncrease);
            //telemetry.addData("heading", gyro.getIntegratedZValue());
            //telemetry.update();
            leftWheel.setPower(leftPower);
            rightWheel.setPower(rightPower);
            //sleep(200);
            //leftWheel.setPower(0);
            //rightWheel.setPower(0);
            //sleep(500);
            //idle();
            RobotSleep(20);
        }
        rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftWheel.setPower(0);
        rightWheel.setPower(0);
        telemetry.addData("heading", gyro.getIntegratedZValue());
        telemetry.addData("distance from wall ", ultrasonic.getUltrasonicLevel());
    }
       */



    // Good Angle is the preferred angle when pushing the beacon button.
    // After ultrasound travel, the angle might be off. This function checks if it is too much off.
    // If so, the robot will do a left or right turn to go to adjustedGoodAngle.
    // The adjustedGoodAngle is 3 degree less than the goodAngle. When turning, the robot typically has 3 degree overshoot.
    // This will make the robot get back to the original goodAngle.




    // Called once during initialization
    private void InitializeRobot() throws InterruptedException {
        // mapping to config file T548.xml

        rf = hardwareMap.dcMotor.get("RightFront");
        lr = hardwareMap.dcMotor.get("LeftBack");
        rr = hardwareMap.dcMotor.get("RightBack");
        lf = hardwareMap.dcMotor.get("LeftFront");
        jsensor = hardwareMap.colorSensor.get("JewelSensor");
        color = hardwareMap.servo.get("ColorServo");
        jewel = hardwareMap.servo.get("JewelKnocker");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);



        // Motor setup
        rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rf.setDirection(DcMotor.Direction.REVERSE);
        lr.setDirection(DcMotor.Direction.REVERSE);


        // set power to zero to avoid a FTC bug

        rf.setPower(0);
        lf.setPower(0);
        lr.setPower(0);
        rr.setPower(0);
        color.setPosition(1);
        jewel.setPosition(0.5);


        // choose team color and delay start time
        //CHANGE
        RobotSleep(500);
        SelectTeamColor();
        String debugMsg = "T548 InitializeRobot. BlueTeam " + blueTeam + " DelayStart " + delayStartTime;
        //DbgLog.msg(debugMsg);


    }


    // Decide particle flywheel power by battery voltage


    // Robot has reached the sensing position. Read the color and push the button of the correct color
    private void ReadColor() throws InterruptedException {
        int beaconColor = ReadBeaconColor();
        if (beaconColor == BEACON_BLUE) {
            if (blueTeam) {
                // Move to the position where the sensor were
                DriveForwardByTime(0.2,2000);
                color.setPosition(1);
            }
            else if(!blueTeam){
                DriveBackwardByTime(0.2,2000);
                RobotSleep(200);
                color.setPosition(1);
            }
            RobotSleep(200);
        } else if (beaconColor == BEACON_RED) {
            // Beacon is red at sensing position
            if (blueTeam) {
                DriveBackwardByTime(0.2,2000);
                color.setPosition(1);
            } else if(!blueTeam){
                // Move to the other button
                DriveForwardByTime(0.2,2000);
                color.setPosition(1);
                RobotSleep(200);
            }
        } else {
            // undecided beacon color, don't prese any button
        }
    }

    /*private void claspGlyph() throws InterruptedException{
        glyphl.setPosition(0.6);
        glyphr.setPosition(0.2);
    }
    //Release Glyph
    private void releaseGlyph() throws InterruptedException{
        glyphl.setPosition(0);
        glyphr.setPosition(1);
    }
    //Put Glyph into box
    private void Touchdown() throws InterruptedException {
        claspGlyph();
        if (leftSide && blueTeam) {
            DriveForwardByEncoder(0.3, 22, 2000);
            TurnByGyroLeft(-90, 0.15);
            DriveForwardByEncoder(0.3, 44, 2000);
            releaseGlyph();
        }
        if (!leftSide && blueTeam) {
            DriveForwardByEncoder(0.3, 30, 2000);
            TurnByGyroLeft(-90, 0.15);
            DriveForwardByEncoder(0.3, 20, 2000);
            releaseGlyph();
        }
        if (!leftSide && !blueTeam) {
            DriveForwardByEncoder(0.3, 22, 2000);
            TurnByGyroLeft(-90, 0.15);
            DriveForwardByEncoder(0.3, 44, 2000);
            releaseGlyph();
        }
        if (leftSide && !blueTeam) {
            DriveForwardByEncoder(0.3, 30, 2000);
            TurnByGyroLeft(-90, 0.15);
            DriveForwardByEncoder(0.3, 20, 2000);
            releaseGlyph();
        } else {
        }
    }*/
    // Team 548 Autonomous main program
    @Override
    public void runOpMode() throws InterruptedException{

        InitializeRobot();
        waitForStart();
        color.setPosition(0);
        sleep(3000);
        ReadColor();
        DriveBackwardByTime(0.2,2000);
    }
}
}


