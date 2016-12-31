package org.usfirst.frc295.TestDrivetrain.subsystems;

import org.usfirst.frc295.TestDrivetrain.RobotMap;
import org.usfirst.frc295.TestDrivetrain.commands.CmdDriveWithJoystick;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SysDriveTrainVictorSP extends Subsystem{
    // ROBOT MAP INSTANTIATED OBJECTS REFERNCED HERE FOR EASY ACCESS
    // Note (Mentor 2016): Not the best practice to do this but will play along
    //                     Since these are just DriveTrain's "HAS A" objects, they should be created here
    //                     I suspect that this is done so all configuration can be done in one place
    //                     Therefore, don't configure and create configurable things anywhere but in RoboMap
    private SpeedController     _escLeftFront;
    private SpeedController     _escLeftBack;
    private SpeedController     _escRightFront;
    private SpeedController     _escRightBack;
    private Encoder             _encoDriveRight;
    private Encoder             _encoDriveLeft;
    private RobotDrive          _robotDrive;


    // SCALE THE SPEED: IN SLOW MODE, SCALE THE INPUT DOWN SO IT IS NOT AS FAST 
    private boolean             _bSlowMode           = false;
    private double              _dMoveScale          = 1.0;
    private double              _dRotationScale      = 1.0;

    // SCALE THE DIRECTION: 1 IS WHEN FRONT IS THE FRONT, -1 IS WHEN THE BACK IS THE FRONT 
    private boolean             _bBackwardMode       = false;
    private double              _dDirectionScale     = 1.0;


    public SysDriveTrainVictorSP()
    {
		super();
    	
        // ==========================================================
        // SYS DRIVE TRAIN 
        // ==========================================================
    	_escLeftFront = new VictorSP(RobotMap.PORT_DRIVE_ESC_LEFT_FRONT);
        LiveWindow.addActuator("SysDriveTrain", "Esc Left Front", (VictorSP) _escLeftFront);
        
        _escLeftBack = new VictorSP(RobotMap.PORT_DRIVE_ESC_LEFT_BACK);
        LiveWindow.addActuator("SysDriveTrain", "Esc Left Back", (VictorSP)  _escLeftBack);
        
        _escRightFront = new VictorSP(RobotMap.PORT_DRIVE_ESC_RIGHT_FRONT);
        LiveWindow.addActuator("SysDriveTrain", "Esc Right Front", (VictorSP) _escRightFront);
        
        _escRightBack = new VictorSP(RobotMap.PORT_DRIVE_ESC_RIGHT_BACK);
        LiveWindow.addActuator("SysDriveTrain", "Esc Right Back", (VictorSP)  _escRightBack);

        

        _robotDrive = new RobotDrive(_escLeftFront,  _escLeftBack,
        										 _escRightFront, _escRightBack);

        _robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft,  true);
        _robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        _robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft,   true);
        _robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight,  true);

        _robotDrive.setSafetyEnabled(true);
        _robotDrive.setExpiration(0.1);
        _robotDrive.setSensitivity(0.5);
        _robotDrive.setMaxOutput(1.0);


        _encoDriveRight = new Encoder(RobotMap.PORT_DRIVE_ENC_RIGHT_CHAN1, RobotMap.PORT_DRIVE_ENC_RIGHT_CHAN2, false, EncodingType.k4X);
        LiveWindow.addSensor("SysDriveTrain", "Enco Drive Right", _encoDriveRight);
        _encoDriveRight.setDistancePerPulse(1.0);
        _encoDriveRight.setPIDSourceType(PIDSourceType.kRate);

        _encoDriveLeft = new Encoder(RobotMap.PORT_DRIVE_ENC_LEFT_CHAN1, RobotMap.PORT_DRIVE_ENC_LEFT_CHAN2, false, EncodingType.k4X);
        LiveWindow.addSensor("SysDriveTrain", "Enco Drive Left", _encoDriveLeft);
        _encoDriveLeft.setDistancePerPulse(1.0);
        _encoDriveLeft.setPIDSourceType(PIDSourceType.kRate);
    }
    
    
    
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
        // DEFAULT FOR THIS SUBSYSTEM IS TO DRIVE WITH JOYSTICK
        setDefaultCommand(new CmdDriveWithJoystick());
    }


	/**
     *  If the input is within the +- the deadband range, then ignore the input
     */
	private double deadbandAdjust(double dInput) 
    {
    	double dDeadbandRange = 0.025;
		return (Math.abs(dInput) < dDeadbandRange) ? 0 : dInput;
	}



    // PUT METHODS FOR CONTROLLING THIS SUBSYSTEM HERE.  GENERALLY CALLED FROM THE COMMANDS
    public void setSlowMode(boolean bSlowMode)   
    {
        if (bSlowMode == true)
        {
            _bSlowMode           = true;
            _dMoveScale          = 0.8;
            _dRotationScale      = 0.6;
        }
        else
        {
            _bSlowMode           = false;
            _dMoveScale          = 1.0;
            _dRotationScale      = 1.0;
        }
    }
    public boolean getSlowMode()
    {
        return(_bSlowMode);
    }   


    public void setBackwardMode(boolean bBackwardMode)   
    {
        if (bBackwardMode == true)
        {
            _bBackwardMode       = true;
            _dDirectionScale     = -1.0;
        }
        else
        {
            _bBackwardMode       = false;
            _dDirectionScale     =  1.0;
        }
    }
    public boolean getBackwardMode()
    {
        return(_bBackwardMode);
    }   


    public void setSpeed(double left, double right) 
    {
		_robotDrive.setLeftRightMotorOutputs(_dDirectionScale * _dRotationScale * deadbandAdjust(left), 
                                             _dDirectionScale * _dRotationScale * deadbandAdjust(right));
	}


	public void drive(double magnitiude, double curve)
    {
		_robotDrive.drive(_dDirectionScale * _dMoveScale     * deadbandAdjust(magnitiude), 
                          _dDirectionScale * _dRotationScale * deadbandAdjust(curve));
	}

    /**
	 * @param joy The ps3 style joystick to use to drive tank style.
	 */
	public void drive(Joystick joy) 
    {
		drive(_dDirectionScale * _dMoveScale     * deadbandAdjust(-joy.getY()),
              _dDirectionScale * _dRotationScale * deadbandAdjust(-joy.getAxis(AxisType.kThrottle)));
	}



    /**
	 * Tank style driving for the DriveTrain.
	 * @param left Speed in range [-1,1]
	 * @param right Speed in range [-1,1]
	 */
	public void tankDrive(double left, double right) 
    {
		if(_bBackwardMode == true) 
        {
			_robotDrive.tankDrive(_dDirectionScale * _dRotationScale * deadbandAdjust(right), 
                                  -1 * _dDirectionScale * _dRotationScale * deadbandAdjust(left));
		} else {
			_robotDrive.tankDrive(_dDirectionScale * _dRotationScale * deadbandAdjust(left),
                                  -1 * _dDirectionScale * _dRotationScale * deadbandAdjust(right));
		}
	}	
	
    
	public void arcadeDrive(double move, double rotation) 
    {
		_robotDrive.arcadeDrive(-.8 * _dDirectionScale * _dMoveScale     * deadbandAdjust(move), 
                                -.8 * _dDirectionScale * _dRotationScale * deadbandAdjust(rotation));
	}

	/**
	 * Reset the robots sensors to the zero states.
	 */
	public void reset() 
    {
//		_gyro.reset();
		_encoDriveRight.reset();
		_encoDriveLeft.reset();
	}


    /**
	 * @return The robots heading in degrees.
	 */
	public double getHeading() 
    {
//		return gyro.getAngle();
		return (0);
	}

	/**
	 * @return The distance driven (average of left and right encoders).
	 */
	public double getDistance() 
    {
		return (_encoDriveRight.getDistance() + _encoDriveLeft.getDistance())/2;
	}

	/**
	 * @return The distance to the obstacle detected by the rangefinder.
	 */
	public double getDistanceToObstacle() 
    {
		// Really meters in simulation since it's a rangefinder...
//		return rangefinder.getAverageVoltage();
		return(0);
	}

    /**
	 * The log method puts interesting information to the SmartDashboard.
	 */
	public void logToSmartDashboard() 
    {
		SmartDashboard.putNumber("Left Distance",  _encoDriveLeft.getDistance());
		SmartDashboard.putNumber("Right Distance", _encoDriveRight.getDistance());
		SmartDashboard.putNumber("Left Speed",     _encoDriveLeft.getRate());
		SmartDashboard.putNumber("Right Speed",    _encoDriveRight.getRate());
//		SmartDashboard.putNumber("Gyro", gyro.getAngle());
	}
}
