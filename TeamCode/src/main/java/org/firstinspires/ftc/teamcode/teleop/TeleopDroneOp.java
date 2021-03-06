package org.firstinspires.ftc.teamcode.teleop;

import android.media.MediaPlayer;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.autonomous.helper.Claw;
import org.firstinspires.ftc.teamcode.robot.RevbotHardware;

/**
 * Created by 3565 on 10/20/2017.
 */
/*

                        _                            _
                   _.-'` `-._                    _,-' `'-._
                ,-'          `-.,____________,.-'    .-.   `-.
               /   .---.             ___            ( Y ) -   \
              /  ,' ,-. `.     __   / X \   __   .-. `-` .-.   \
             /   | |   | |    (__) | / \ | (__) ( X )   ( B )   \
            /    `. `-' ,'    __    \___/        `-` ,-. `-`     \
            |      `---`   ,-`  `-.       .---.     ( A )        |
            |             / -'  `- \    ,'  .  `.    `-`         |
            |            |          |   | -   - |                |
            !             \ -.  ,- /    `.  '  ,'                |
            |              `-.__,-'       `---`                  |
            |                  ________________                  |
            |             _,-'`                ``-._             |
            |          ,-'                          `-.          |
             \       ,'                                `.       /
              `.__,-'                                    `-.__,'
 */
@TeleOp(name="Drone Op", group="TeleOp")
public class TeleopDroneOp extends LinearOpMode {

    RevbotHardware robot = new RevbotHardware();
    Claw claw = new Claw();
    
    public double leftPower;
    public double rightPower;
    public double strafePower;
    public double turnPower;
    public double hyperPrecision;
    public boolean smartDirect;
    public double[] directSave = new double[3];

    public final double HP_STRENGTH=4;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        claw.runOpMode();
        
        directSave[0] = 0;
        directSave[1] = 0;
        directSave[2] = 0;

        telemetry.addData("Status:", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            hyperPrecision = gamepad1.left_trigger*HP_STRENGTH  + 1;
            leftPower = gamepad1.left_stick_y;
            rightPower = -gamepad1.left_stick_y;
            smartDirect = gamepad1.left_bumper;

            turnPower = gamepad1.right_stick_x;

            strafePower = -gamepad1.left_stick_x;
            leftPower -= turnPower;
            rightPower -= turnPower;

            strafePower /= hyperPrecision;
            leftPower /= hyperPrecision;
            rightPower /= hyperPrecision;
            turnPower /= hyperPrecision;

            //Tests to see if directSave is on. If so, use saved movement.

            if(gamepad1.right_trigger > 0.1){
                robot.leftDrive.setPower(directSave[0] * gamepad1.right_trigger);
                robot.rightDrive.setPower(directSave[1] * gamepad1.right_trigger);
                robot.strafe.setPower(directSave[2] * gamepad1.right_trigger);
            }else{

                if (!smartDirect || (smartDirect && Math.abs(gamepad1.left_stick_y) >= Math.abs(gamepad1.left_stick_x))){

                    robot.leftDrive.setPower(leftPower);
                    robot.rightDrive.setPower(rightPower);

                    if(smartDirect && Math.abs(gamepad1.left_stick_y) >= Math.abs(gamepad1.left_stick_x)){
                        robot.strafe.setPower(0);
                    }

                }

                if (!smartDirect || (smartDirect && Math.abs(gamepad1.left_stick_y) < Math.abs(gamepad1.left_stick_x))){

                    robot.strafe.setPower(-strafePower);

                    if(smartDirect && Math.abs(gamepad1.left_stick_y) < Math.abs(gamepad1.left_stick_x)){
                        robot.leftDrive.setPower(0);
                        robot.rightDrive.setPower(0);
                    }

                }

                if(gamepad1.right_bumper){

                    directSave[0] = robot.leftDrive.getPower();
                    directSave[1] = robot.rightDrive.getPower();
                    directSave[2] = robot.strafe.getPower();

                }
            }
            if(gamepad1.dpad_up) {
                robot.cubeLift.setPower(-1.0);
            }
            else if(gamepad1.dpad_down) {
                robot.cubeLift.setPower(0.5);
            }
            else {
                robot.cubeLift.setPower(0.0);
            }

            if(gamepad1.dpad_left){
                claw.closeClaw();
            }
            if(gamepad1.dpad_right){
                claw.openClaw();
            }

            telemetry.addData("Status", "Running");
            telemetry.addData("ServoPosition", robot.clawRight.getPosition());
            telemetry.update();


        }
    }
}