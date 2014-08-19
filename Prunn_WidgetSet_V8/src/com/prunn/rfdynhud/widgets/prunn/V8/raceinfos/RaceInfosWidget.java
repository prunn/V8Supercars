package com.prunn.rfdynhud.widgets.prunn.V8.raceinfos;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetv8;

import net.ctdp.rfdynhud.gamedata.Laptime;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceInfosWidget extends Widget
{
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    
    private DrawnString dsName = null;
    private DrawnString dsTeam = null;
    private DrawnString dsFastestLaps = null;
    private DrawnString dsFastLapTime1 = null;
    private DrawnString dsFastLapTime2 = null;
    private DrawnString dsFastLap1 = null;
    private DrawnString dsFastLap2 = null;
    private DrawnString dsFastName1 = null;
    private DrawnString dsFastName2 = null;
    private DrawnString dsFastCar1 = null;
    private DrawnString dsFastCar2 = null;
    private DrawnString dsWinner = null;
    private DrawnString dsStop = null;
    private DrawnString dsPit = null;
    private DrawnString dsTitleC = null;
    private final ImagePropertyWithTexture imgName = new ImagePropertyWithTexture( "imgName", "prunn/V8/name.png" );
    private final ImagePropertyWithTexture imgNameBlack = new ImagePropertyWithTexture( "imgName", "prunn/V8/name.png" );
    private final ImagePropertyWithTexture imgTitle = new ImagePropertyWithTexture( "imgTitle", "prunn/V8/Results_title.png" );
    private final ImagePropertyWithTexture imgTitleF = new ImagePropertyWithTexture( "imgTitle", "prunn/V8/Results_title.png" );
    private final ImagePropertyWithTexture imgFastHolden = new ImagePropertyWithTexture( "imgTitle", "prunn/V8/fastest_holden.png" );
    private final ImagePropertyWithTexture imgFastFord = new ImagePropertyWithTexture( "imgTitle", "prunn/V8/fastest_ford.png" );
    private final ImagePropertyWithTexture imgPit = new ImagePropertyWithTexture( "imgTitle", "prunn/V8/pit_stop.png" );
    private final ImagePropertyWithTexture imgFord = new ImagePropertyWithTexture( "imgTeam", "prunn/V8/team_ford.png" );
    private final ImagePropertyWithTexture imgHolden = new ImagePropertyWithTexture( "imgTeam", "prunn/V8/team_holden.png" );
    private final ImagePropertyWithTexture imgBlackFlag = new ImagePropertyWithTexture( "imgTeam", "prunn/V8/Black_Flag.png" );
    private IntProperty JackFallSpeed = new IntProperty("Jack Fall Speed", 18) ;
    private final FontProperty InfoNameFont = new FontProperty("InfoNameFont", PrunnWidgetSetv8.INFO_NAME_FONT);
    protected final FontProperty f1_2011Font = new FontProperty("Main Font", PrunnWidgetSetv8.V8_FONT_NAME);
    protected final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetv8.FONT_COLOR2_NAME);
    protected final ColorProperty fontColor3 = new ColorProperty("fontColor3", PrunnWidgetSetv8.FONT_COLOR3_NAME);
    protected final BooleanProperty showwinner = new BooleanProperty("Show Winner", "showwinner", true);
    protected final BooleanProperty showfastest = new BooleanProperty("Show Fastest Lap", "showfastest", true);
    protected final BooleanProperty showpitstop = new BooleanProperty("Show Pitstop", "showpitstop", true);
    protected final BooleanProperty showinfo = new BooleanProperty("Show Info", "showinfo", true);
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private final FloatValue sessionTime = new FloatValue(-1F, 0.1F);
    private float timestamp = -1;
    private float endtimestamp = -1;
    private float pittime = -1;
    private long visibleEndPitStop;
    private BoolValue isInPit = new BoolValue(false);
    private final DelayProperty visibleTime;
    private long visibleEnd;
    private long visibleEndPenalty;
    private IntValue cveh = new IntValue();
    private IntValue speed = new IntValue();
    private long visibleEndW;
    private long visibleEndF;
    private final FloatValue racetime = new FloatValue( -1f, 0.1f );
    private float sessionstart = 0;
    private BoolValue racefinished = new BoolValue();
    private int widgetpart = 0;//0-info 1-pitstop 2-fastestlap 3-winner
    private final FloatValue FastestLapTime = new FloatValue(-1F, 0.001F);
    private IntValue Penalties[];
    private IntValue Pentotal =  new IntValue();
    private int flaggeddriver = 0;
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int fh = TextureImage2D.getStringHeight( "0%C", f1_2011Font );
        int fhPos = TextureImage2D.getStringHeight( "0%C", InfoNameFont );
        int rowHeight = height / 3;
        int numveh = gameData.getScoringInfo().getNumVehicles();
        
        
        imgFord.updateSize( width, height*30/100, isEditorMode );
        imgHolden.updateSize( width, height*32/100, isEditorMode );
        
        imgName.updateSize( width, height*42/100, isEditorMode );
        imgNameBlack.updateSize( width*80/100, rowHeight, isEditorMode );
        imgTitle.updateSize( width, height*26/100, isEditorMode );
        imgTitleF.updateSize( width, rowHeight, isEditorMode );
        imgFastHolden.updateSize( width, rowHeight, isEditorMode );
        imgFastFord.updateSize( width, rowHeight, isEditorMode );
        imgPit.updateSize( width*20/100, height, isEditorMode );
        imgBlackFlag.updateSize( width*20/100, height, isEditorMode );
        int top1 = ( rowHeight - fh ) / 2 + fontyoffset.getValue();
        int top1m = height*24/100 - fhPos / 2;
        int top2m = height*59/100 - fh / 2;
        int top2 = ( rowHeight - fh ) / 2 + rowHeight + fontyoffset.getValue();
        int top3 = ( rowHeight - fh ) / 2 + rowHeight*2 + fontyoffset.getValue();
        int top3m = height*88/100 - fh / 2;
        
        
        dsName = drawnStringFactory.newDrawnString( "dsName", width*4/100, top1m, Alignment.LEFT, false, InfoNameFont.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTeam = drawnStringFactory.newDrawnString( "dsTeam", width*4/100, top2m, Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsFastestLaps = drawnStringFactory.newDrawnString( "dsFastestLaps", width*3/100, top1, Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor3.getColor(), null, "" );
        dsFastLapTime1 = drawnStringFactory.newDrawnString( "dsFastLap1", width*98/100, top2, Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsFastLapTime2 = drawnStringFactory.newDrawnString( "dsFastLap2", width*98/100, top3, Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        
        dsFastLap1 = drawnStringFactory.newDrawnString( "dsFastLap1", width*149/200, top2, Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor3.getColor(), null, "" );
        dsFastLap2 = drawnStringFactory.newDrawnString( "dsFastLap2", width*149/200, top3, Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor3.getColor(), null, "" );
        dsFastName1 = drawnStringFactory.newDrawnString( "dsFastLap1", width*10/100, top2, Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsFastName2 = drawnStringFactory.newDrawnString( "dsFastLap2", width*10/100, top3, Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsFastCar1 = drawnStringFactory.newDrawnString( "dsFastLap1", width*4/100, top2, Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsFastCar2 = drawnStringFactory.newDrawnString( "dsFastLap2", width*4/100, top3, Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        
        dsWinner = drawnStringFactory.newDrawnString( "dsWinner", width*4/100, top3m, Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor3.getColor(), null, "" );
        dsPit = drawnStringFactory.newDrawnString( "dsTimeC", width*10/100, top1, Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsStop = drawnStringFactory.newDrawnString( "dsTimeC", width*10/100, top2, Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        dsTitleC = drawnStringFactory.newDrawnString( "dsTitleC", width*10/100, top3, Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor(), null, "" );
        Penalties = new IntValue[numveh];
        for(int i=0;i < numveh;i++)
        { 
            Penalties[i] = new IntValue();
            Penalties[i].update(0);
            Penalties[i].setUnchanged();
        }
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        super.updateVisibility(gameData, isEditorMode);
        
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int numveh = gameData.getScoringInfo().getNumVehicles();
        cveh.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getDriverId());
        isInPit.update(scoringInfo.getViewedVehicleScoringInfo().isInPits());
        //fastest lap
        Laptime lt = scoringInfo.getFastestLaptime();
        
        if(lt == null || !lt.isFinished())
            FastestLapTime.update(-1F);
        else
            FastestLapTime.update(lt.getLapTime());
        //winner part
        if(gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapsCompleted() < 1)
            sessionstart = gameData.getScoringInfo().getLeadersVehicleScoringInfo().getLapStartTime();
        if(scoringInfo.getSessionTime() > 0)
            racetime.update( scoringInfo.getSessionTime() - sessionstart );
        
        racefinished.update(gameData.getScoringInfo().getViewedVehicleScoringInfo().getFinishStatus().isFinished());
        
               
        //carinfo
        if(cveh.hasChanged() && cveh.isValid() && showinfo.getValue() && !isEditorMode)
        {
            forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            widgetpart = 0;
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd )
        {
            forceCompleteRedraw(true);
            isvisible = true;
            widgetpart = 0;
            return true;
        }
        
      //pitstop   
        if( isInPit.hasChanged())
        {
            if(isInPit.getValue())
            {
                pittime = 0;
                forceCompleteRedraw(true);
            }
            
            endtimestamp = 0;
            timestamp = 0;
            
        }
            
        if( isInPit.getValue() && showpitstop.getValue() )
        {
            if(scoringInfo.getViewedVehicleScoringInfo().getLapsCompleted() > 0)
                widgetpart = 1;
            else
            {
                widgetpart = 0;
            }
            
            speed.update( (int)scoringInfo.getViewedVehicleScoringInfo().getScalarVelocity());
            if(speed.hasChanged() && speed.getValue() < JackFallSpeed.getValue())//2
            {//ai gets to 5-6 kmh when they drop
                endtimestamp = gameData.getScoringInfo().getSessionTime();
                timestamp = gameData.getScoringInfo().getSessionTime();
            }
            else
                if(speed.getValue() < 2)
                    endtimestamp = gameData.getScoringInfo().getSessionTime();
                
            
            visibleEndPitStop = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            return true;
        }
        
        if(scoringInfo.getSessionNanos() < visibleEndPitStop )
        {
            forceCompleteRedraw(true);
            isvisible = true;
            widgetpart = 1;
            return true;
        }
        //fastest lap
        if(scoringInfo.getSessionNanos() < visibleEndF && FastestLapTime.isValid())
        {
            isvisible = true;
            widgetpart = 2;
            return true; 
        }
        if(FastestLapTime.hasChanged() && FastestLapTime.isValid() && scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() > 1 && showfastest.getValue())
        {
            forceCompleteRedraw(true);
            visibleEndF = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            isvisible = true;
            widgetpart = 2;
            return true;
        }
        
        //winner part
        if(scoringInfo.getSessionNanos() < visibleEndW )
        {
            isvisible = true;
            widgetpart = 3;
            return true;
        }
         
        if(racefinished.hasChanged() && racefinished.getValue() && showwinner.getValue() )
        {
            forceCompleteRedraw(true);
            visibleEndW = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos()*2;
            isvisible = true;
            widgetpart = 3;
            return true;
        }
        //Penalties
        if(scoringInfo.getSessionType().isRace())
        {
            
            int total=0;
            for(int j=0;j < numveh;j++)
            {
                total += scoringInfo.getVehicleScoringInfo( j ).getNumOutstandingPenalties();
            }
            Pentotal.update( total );
           
            if(Pentotal.getValue() > Pentotal.getOldValue() && Pentotal.hasChanged() && Pentotal.getValue() > 0)
            {
               forceCompleteRedraw(true);
               widgetpart = 4;
               visibleEndPenalty = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
               return true;
            }
            else
                Pentotal.hasChanged();
        }
        if(scoringInfo.getSessionNanos() < visibleEndPenalty)
        {
            widgetpart = 4;
            return true;
        }
        
        isvisible = false;
        return false;	
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        int rowHeight = height / 3;
        if(isEditorMode)
            widgetpart = 2;
        switch(widgetpart)
        {
            case 1: //Pit Stop
                    texture.clear( imgPit.getTexture(),offsetX , offsetY, false, null );
                    break;
    
            case 2: //Fastest Lap
                    ScoringInfo scoringInfo = gameData.getScoringInfo();
                    
                    
                    texture.clear( imgTitleF.getTexture(), offsetX, offsetY, false, null );
                    if(scoringInfo.getFastestLapVSI().getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ))
                        texture.clear( imgFastFord.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    else
                        texture.clear( imgFastHolden.getTexture(), offsetX, offsetY + rowHeight, false, null );
                    
                    if(scoringInfo.getSecondFastestLapVSI().getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ))
                        texture.clear( imgFastFord.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                    else
                        texture.clear( imgFastHolden.getTexture(), offsetX, offsetY + rowHeight*2, false, null );
                    break;
                    
            case 3: //Winner
                    texture.clear( imgName.getTexture(), offsetX, offsetY, false, null );
                    if(gameData.getScoringInfo().getViewedVehicleScoringInfo().getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ))
                        texture.clear( imgFord.getTexture(), offsetX, offsetY + height*42/100, false, null );
                    else
                        texture.clear( imgHolden.getTexture(), offsetX, offsetY + height*42/100, false, null );
                    
                    texture.clear( imgTitle.getTexture(), offsetX, offsetY + height*74/100, false, null );
                    
                    break;
            case 4: //BlackFlag
                    texture.clear( imgNameBlack.getTexture(), offsetX + width*20/100, offsetY + rowHeight*2, false, null );
                    texture.clear( imgBlackFlag.getTexture(), offsetX, offsetY, false, null );
                    break;
    
        
            default: //Info
                    texture.clear( imgName.getTexture(), offsetX, offsetY, false, null );
                    if(gameData.getScoringInfo().getViewedVehicleScoringInfo().getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ))
                        texture.clear( imgFord.getTexture(), offsetX, offsetY + height*42/100, false, null );
                    else
                        texture.clear( imgHolden.getTexture(), offsetX, offsetY + height*42/100, false, null );
                    
                    break;
        }
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
    	sessionTime.update(scoringInfo.getSessionTime());
    	if(isEditorMode)
            widgetpart = 2;
    	
    	if ( needsCompleteRedraw || sessionTime.hasChanged() || FastestLapTime.hasChanged())
        {
    	    String top1info1="";
    	    String top2info1;
    	    String top3info2c="";
            String top3info1;
    	    String top3info2="";
    	    
    	    switch(widgetpart)
            {
                case 1: //Pit Stop
                    if(scoringInfo.getViewedVehicleScoringInfo().getScalarVelocity() < 2)
                        pittime = endtimestamp - timestamp;
                    
                    top3info2c = TimingUtil.getTimeAsString(pittime, false, false, true, false );
                    if(isEditorMode)
                        top3info2c = "5.8";
                    dsPit.draw( offsetX, offsetY, "PIT", texture);
                    dsStop.draw( offsetX, offsetY, "STOP", texture);
                    dsTitleC.draw( offsetX, offsetY, top3info2c, texture );
                    
                    break;
                
            case 2: //Fastest Lap
                        VehicleScoringInfo fastcarinfos = scoringInfo.getFastestLapVSI();
                        VehicleScoringInfo secFastCarInfos = scoringInfo.getSecondFastestLapVSI();
                        top3info2 = TimingUtil.getTimeAsLaptimeString(FastestLapTime.getValue() );
                        
                        dsFastestLaps.draw( offsetX, offsetY, "FASTEST LAPS", texture);
                        dsFastLapTime1.draw( offsetX, offsetY, top3info2, texture );
                        dsFastLapTime2.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(secFastCarInfos.getFastestLaptime().getLapTime()), texture );
                        dsFastLap1.draw( offsetX, offsetY, String.valueOf( fastcarinfos.getFastestLaptime().getLap() ), texture );
                        dsFastLap2.draw( offsetX, offsetY, String.valueOf( secFastCarInfos.getFastestLaptime().getLap() ), texture );
                        dsFastName1.draw( offsetX, offsetY, fastcarinfos.getDriverNameShort().toUpperCase(), texture );
                        dsFastName2.draw( offsetX, offsetY, secFastCarInfos.getDriverNameShort().toUpperCase(), texture );
                        dsFastCar1.draw( offsetX, offsetY, String.valueOf( fastcarinfos.getVehicleInfo().getCarNumber() ), texture );
                        dsFastCar2.draw( offsetX, offsetY, String.valueOf( secFastCarInfos.getVehicleInfo().getCarNumber() ), texture );
                        
                        break;
                        
                case 3: //Winner
                        VehicleScoringInfo winnercarinfos = gameData.getScoringInfo().getLeadersVehicleScoringInfo();
                        
                        if(winnercarinfos.getVehicleInfo() != null)
                            top3info1 = winnercarinfos.getVehicleInfo().getTeamName();
                        else
                            top3info1 = winnercarinfos.getVehicleClass(); 
                        
                        float laps=0;
                        
                        for(int i=1;i <= winnercarinfos.getLapsCompleted(); i++)
                        {
                            if(winnercarinfos.getLaptime(i) != null)
                                laps = winnercarinfos.getLaptime(i).getLapTime() + laps;
                            else
                            {
                                laps = racetime.getValue();
                                i = winnercarinfos.getLapsCompleted()+1;
                            }
                        } 
                        top1info1 = "WINNER";
                        top2info1 = winnercarinfos.getDriverNameShort().toUpperCase();
                        dsName.draw( offsetX, offsetY, top2info1, texture );
                        dsTeam.draw( offsetX, offsetY, top3info1, texture );
                        dsWinner.draw( offsetX, offsetY, top1info1, texture );
                        
                        break;
                case 4: ///////////////Penalty
                    int numveh = gameData.getScoringInfo().getNumVehicles();
                
                    for(int i=0;i < numveh;i++)
                    {
                       Penalties[i].update( scoringInfo.getVehicleScoringInfo( i ).getNumOutstandingPenalties() );
                                    
                       if(Penalties[i].hasChanged() && Penalties[i].getValue() > 0 )
                           flaggeddriver = i;
                    }
                    VehicleScoringInfo vsi = gameData.getScoringInfo().getVehicleScoringInfo( flaggeddriver );
                    
                    dsFastName2.draw( offsetX, offsetY, "         " + vsi.getDriverName().toUpperCase(), texture );
                    
                    break;
                
            
                default: //Info
                        VehicleScoringInfo currentcarinfosInfo = gameData.getScoringInfo().getViewedVehicleScoringInfo();
                        
                        if(currentcarinfosInfo.getVehicleInfo() != null)
                            top3info1 = currentcarinfosInfo.getVehicleInfo().getTeamName();
                        else
                            top3info1 = currentcarinfosInfo.getVehicleClass(); 
                        
                        top2info1 = currentcarinfosInfo.getDriverNameShort().toUpperCase();
                        
                        dsName.draw( offsetX, offsetY, top2info1, texture );
                        dsTeam.draw( offsetX, offsetY, top3info1, texture );
                        
                        break;
            }
    	    
        	
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( f1_2011Font, "" );
        writer.writeProperty( InfoNameFont, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty(visibleTime, "");
        writer.writeProperty(showwinner, "");
        writer.writeProperty(showfastest, "");
        writer.writeProperty(showpitstop, "");
        writer.writeProperty(showinfo, "");
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( JackFallSpeed, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( f1_2011Font ) );
        else if ( loader.loadProperty( InfoNameFont ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if(loader.loadProperty(visibleTime));
        else if ( loader.loadProperty( showwinner ) );
        else if ( loader.loadProperty( showfastest ) );
        else if ( loader.loadProperty( showpitstop ) );
        else if ( loader.loadProperty( JackFallSpeed ) );
        else if ( loader.loadProperty( showinfo ) );
        else if ( loader.loadProperty( fontyoffset ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Colors" );
        propsCont.addProperty( f1_2011Font );
        propsCont.addProperty( InfoNameFont );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColor3 );
        propsCont.addProperty(visibleTime);
        propsCont.addProperty(showwinner);
        propsCont.addProperty(showfastest);
        propsCont.addProperty(showpitstop);
        propsCont.addProperty(JackFallSpeed);
        propsCont.addProperty(showinfo);
        propsCont.addProperty( fontyoffset );
        
    }
    @Override
    protected boolean canHaveBorder()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        
        getFontProperty().setFont( "Dialog", Font.PLAIN, 6, false, true );
        
    }
    
    public RaceInfosWidget()
    {
        super(PrunnWidgetSetv8.INSTANCE, PrunnWidgetSetv8.WIDGET_PACKAGE_V8, 36.0f, 11.6f);
        
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 6);
        visibleEnd = 0;
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetv8.V8_FONT_NAME );
    }
    
}
