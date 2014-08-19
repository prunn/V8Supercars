package com.prunn.rfdynhud.widgets.prunn.V8.qtime;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetv8;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.ColorProperty;
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
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.EnumValue;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class QualTimeWidget extends Widget
{
    private static enum Situation
    {
        LAST_SECONDS_OF_SECTOR_1,
        SECTOR_1_FINISHED_BEGIN_SECTOR_2,
        LAST_SECONDS_OF_SECTOR_2,
        SECTOR_2_FINISHED_BEGIN_SECTOR_3,
        LAST_SECONDS_OF_SECTOR_LAP,
        LAP_FINISHED_BEGIN_NEW_LAP,
        OTHER,
        ;
    }
    
    private static final float SECTOR_DELAY = 5f;
    
    private DrawnString dsPos = null;
    private DrawnString dsPosTHFinish = null;
    private DrawnString dsCurrentPos = null;
    private DrawnString dsPosTH = null;
    private DrawnString dsPosFrom = null;
    private DrawnString dsPosST = null;
    private DrawnString dsName = null;
    private DrawnString dsTitle = null;
    private DrawnString dsLeaderName = null;
    private DrawnString dsLeaderTime = null;
    private DrawnString dsTime = null;
    private DrawnString dsGap = null;
    
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/V8/qpos.png" );
    private final ImagePropertyWithTexture imgTime = new ImagePropertyWithTexture( "imgTime", "prunn/V8/qualif.png" );
    private final ImagePropertyWithTexture imgFastest = new ImagePropertyWithTexture( "imgFastest", "prunn/V8/qualif_fastest.png" );
    private final ImagePropertyWithTexture imgFaster = new ImagePropertyWithTexture( "imgFaster", "prunn/V8/qualif_faster.png" );
    private final ImagePropertyWithTexture imgSlower = new ImagePropertyWithTexture( "imgSlower", "prunn/V8/qualif_slower.png" );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private String Title;
    
    private final ColorProperty fontColor2 = new ColorProperty("fontColor2", PrunnWidgetSetv8.FONT_COLOR2_NAME);
    private final FontProperty posFont = new FontProperty("positionFont", PrunnWidgetSetv8.POS_FONT_NAME);
    private final FontProperty posFontTH = new FontProperty("v8thFont", PrunnWidgetSetv8.V8_FONT_NAME_TH);
    private final FontProperty posFontTHBig = new FontProperty("v8thFontBig", PrunnWidgetSetv8.V8_FONT_NAME_TH_BIG);
    private final ColorProperty fontColor3 = new ColorProperty("fontColor3", PrunnWidgetSetv8.FONT_COLOR3_NAME);
    
    
    private final EnumValue<Situation> situation = new EnumValue<Situation>();
    private final IntValue leaderID = new IntValue();
    private final IntValue leaderPos = new IntValue();
    private final IntValue ownPos = new IntValue();
    private float leadsec1 = -1f;
    private float leadsec2 = -1f;
    private float leadlap = -1f;
    private final FloatValue cursec1 = new FloatValue(-1f, 0.001f);
    private final FloatValue cursec2 = new FloatValue(-1f, 0.001f);
    private final FloatValue curlap = new FloatValue(-1f, 0.001f);
    private final FloatValue oldbesttime = new FloatValue(-1f, 0.001f);
    private final FloatValue gapOrTime = new FloatValue(-1f, 0.001f);
    private final FloatValue lastLaptime = new FloatValue(-1f, 0.001f);
    private final FloatValue fastestlap = new FloatValue(-1f, 0.001f);
    private float oldbest = 0;
    private static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    public String ShortName( String driverName )
    {
        int sp = driverName.lastIndexOf( ' ' );
        if ( sp == -1 )
        {
            return ( driverName );
        }
        
        String sf = driverName.substring( sp + 1 );
        
        return ( sf );
    }
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        situation.reset();
        leaderID.reset();
        leaderPos.reset();
        ownPos.reset();
        cursec1.reset();
        cursec2.reset();
        curlap.reset();
        oldbesttime.reset();
        gapOrTime.reset();
        lastLaptime.reset();
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
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int rowHeight = height / 3;
        int fh = TextureImage2D.getStringHeight( "09gy", getFontProperty() );
        int posfh = TextureImage2D.getStringHeight( "0", posFont );
        int top1 = ( rowHeight - fh ) / 2 + 2;
        int top2 = rowHeight + ( rowHeight - fh ) / 2 + fontyoffset.getValue() + 4;
        int top3 = rowHeight*2 +( rowHeight - fh ) / 2 + fontyoffset.getValue() + 4;
        imgPos.updateSize( width*14/100, height, isEditorMode );
        imgTime.updateSize( width*86/100, height, isEditorMode );
        imgFastest.updateSize( width*8/200, height*30/100, isEditorMode );
        imgFaster.updateSize( width*8/200, height*30/100, isEditorMode );
        imgSlower.updateSize( width*8/200, height*30/100, isEditorMode );
        
        Color blackFontColor = fontColor3.getColor();
        Color whiteFontColor = fontColor2.getColor();
        
        
        dsTitle = drawnStringFactory.newDrawnString( "dsTitle", width*3/100, top1, Alignment.LEFT, false, getFont(), isFontAntiAliased(), blackFontColor);
        dsName = drawnStringFactory.newDrawnString( "dsName", width*9/100, top3, Alignment.LEFT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsLeaderName = drawnStringFactory.newDrawnString( "dsName", width*9/100, top2, Alignment.LEFT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsLeaderTime = drawnStringFactory.newDrawnString( "dsTime", width*57/100, top2, Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsTime = drawnStringFactory.newDrawnString( "dsTime", width*57/100, top3, Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        dsPos = drawnStringFactory.newDrawnString( "dsPos", width*89/100, height/2 - posfh/2 + fontyoffset.getValue(), Alignment.LEFT, false, posFont.getFont(), isFontAntiAliased(), blackFontColor);
        dsPosTHFinish = drawnStringFactory.newDrawnString( "dsPosTHFinish", width*94/100, height/2 - posfh/2 + height*13/100 + fontyoffset.getValue(), Alignment.LEFT, false, posFontTHBig.getFont(), isFontAntiAliased(), blackFontColor);
        dsCurrentPos = drawnStringFactory.newDrawnString( "dsCurrentPos", width*2/100, top3, Alignment.LEFT, false, getFont(), isFontAntiAliased(), blackFontColor );
        dsPosTH = drawnStringFactory.newDrawnString( "dsPosTH", width*4/100, top3, Alignment.LEFT, false, posFontTH.getFont(), isFontAntiAliased(), blackFontColor );
        dsPosFrom = drawnStringFactory.newDrawnString( "dsPosFrom", width*2/100, top2, Alignment.LEFT, false, getFont(), isFontAntiAliased(), blackFontColor );
        dsPosST = drawnStringFactory.newDrawnString( "dsPosFrom", width*4/100, top2, Alignment.LEFT, false, posFontTH.getFont(), isFontAntiAliased(), blackFontColor );
        dsGap = drawnStringFactory.newDrawnString( "dsTime", width*73/100, top3, Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor);
        
        switch(scoringInfo.getSessionType())
        {
            case QUALIFYING1: case QUALIFYING2: case QUALIFYING3: case QUALIFYING4:
                Title = "QUALIFYING";
                break;
            case PRACTICE1:
                Title = "PRACTICE 1";
                break;
            case PRACTICE2:
                Title = "PRACTICE 2";
                break;
            case PRACTICE3:
                Title = "PRACTICE 3";
                break;
            case PRACTICE4:
                Title = "PRACTICE 4";
                break;
            case TEST_DAY:
                Title = "TEST DAY";
                break;
            case WARMUP:
                Title = "WARM UP";
                break;
            default:
                Title = "";
                break;
                    
        }
        if(isEditorMode)
            Title = "QUALIFYING";  
    }
    
    private void updateSectorValues( ScoringInfo scoringInfo )
    {
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo leadercarinfos = scoringInfo.getLeadersVehicleScoringInfo();
        
        if(leadercarinfos.getFastestLaptime() != null && leadercarinfos.getFastestLaptime().getLapTime() >= 0)
        {
            leadsec1 = leadercarinfos.getFastestLaptime().getSector1();
            leadsec2 = leadercarinfos.getFastestLaptime().getSector1And2();
            leadlap = leadercarinfos.getFastestLaptime().getLapTime();
        }
        else
        {
            leadsec1 = 0f;
            leadsec2 = 0f;
            leadlap = 0f;
        }
        
        cursec1.update( currentcarinfos.getCurrentSector1() );
        cursec2.update( currentcarinfos.getCurrentSector2( true ) );

        if ( scoringInfo.getSessionTime() > 0f )
            curlap.update( currentcarinfos.getCurrentLaptime() );
        else
            curlap.update( scoringInfo.getSessionNanos() / 1000000000f - currentcarinfos.getLapStartTime() ); 
            

    }
    
    private boolean updateSituation( VehicleScoringInfo currentcarinfos )
    {
        final byte sector = currentcarinfos.getSector();
        
        if(sector == 1 && curlap.getValue() > leadsec1 - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_1 );
        }
        else if(sector == 2 && curlap.getValue() - cursec1.getValue() <= SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.SECTOR_1_FINISHED_BEGIN_SECTOR_2 );
        }
        else if(sector == 2  && curlap.getValue() > leadsec2 - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_2 );
        }
        else if(sector == 3 && curlap.getValue() - cursec2.getValue() <= SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.SECTOR_2_FINISHED_BEGIN_SECTOR_3 );
        }
        else if(sector == 3 && curlap.getValue() > leadlap - SECTOR_DELAY && leadlap > 0)
        {
            situation.update( Situation.LAST_SECONDS_OF_SECTOR_LAP );
        }
        else if(sector == 1 && curlap.getValue() <= SECTOR_DELAY && currentcarinfos.getLastLapTime() > 0)
        {
            situation.update( Situation.LAP_FINISHED_BEGIN_NEW_LAP );
        }
        else
        {
            situation.update( Situation.OTHER );
        }
        
        return ( situation.hasChanged() );
    }
    
    @Override
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        updateSectorValues( scoringInfo );
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
       
        
        fastestlap.update(scoringInfo.getLeadersVehicleScoringInfo().getBestLapTime());
        
        if ( (updateSituation( currentcarinfos )  || fastestlap.hasChanged() ) && !isEditorMode)
            forceCompleteRedraw( true );
        
        if ( currentcarinfos.isInPits() )
        {
            isvisible = false;
            return false;
        }
        
        if(currentcarinfos.getFinishStatus() == FinishStatus.FINISHED && situation.getValue() != Situation.LAP_FINISHED_BEGIN_NEW_LAP )
            return false;
        
        float curLaptime;
        if ( scoringInfo.getSessionTime() > 0f )
            curLaptime = currentcarinfos.getCurrentLaptime();
        else
            curLaptime = scoringInfo.getSessionNanos() / 1000000000f - currentcarinfos.getLapStartTime();
        
        if ( curLaptime > 0f )
        {
            isvisible = true;
            //forceCompleteRedraw( true );
            return true;
        }
            
        return false;
         
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        //VehicleScoringInfo leadercarinfos = scoringInfo.getLeadersVehicleScoringInfo();
       
        //int rowHeight = height / 3;
        
        texture.clear( imgTime.getTexture(), offsetX, offsetY, false, null );
        
        switch ( situation.getValue() )
        {
            case SECTOR_1_FINISHED_BEGIN_SECTOR_2:
                
                if( cursec1.getValue() <= leadsec1 )
                    texture.clear( imgFastest.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                else if( currentcarinfos.getFastestLaptime() == null || cursec1.getValue() <= currentcarinfos.getFastestLaptime().getSector1() )
                    texture.clear( imgFaster.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                else
                    texture.clear( imgSlower.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                
                
                break;
                
            case SECTOR_2_FINISHED_BEGIN_SECTOR_3:
                
                if( cursec1.getValue() <= leadsec1 )
                    texture.clear( imgFastest.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                else if( currentcarinfos.getFastestLaptime() == null || cursec1.getValue() <= currentcarinfos.getFastestLaptime().getSector1() )
                    texture.clear( imgFaster.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                else
                    texture.clear( imgSlower.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                
                if( cursec2.getValue() <= leadsec2 )
                    texture.clear( imgFastest.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                else if( currentcarinfos.getFastestLaptime() == null || cursec2.getValue() <= currentcarinfos.getFastestLaptime().getSector2( true ) )
                    texture.clear( imgFaster.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                else
                    texture.clear( imgSlower.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                 
                
                break;
                
            case LAP_FINISHED_BEGIN_NEW_LAP:
                   
                if( currentcarinfos.getLastSector1() <= leadsec1 )
                    texture.clear( imgFastest.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                else if( currentcarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector1() <= currentcarinfos.getFastestLaptime().getSector1() )
                    texture.clear( imgFaster.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                else
                    texture.clear( imgSlower.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                
                if( currentcarinfos.getLastSector2(true) <= leadsec2 )
                    texture.clear( imgFastest.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                else if( currentcarinfos.getFastestLaptime() == null || currentcarinfos.getLastSector2(true) <= currentcarinfos.getFastestLaptime().getSector2( true ) )
                    texture.clear( imgFaster.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                else
                    texture.clear( imgSlower.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                 
                if( currentcarinfos.getLastLapTime() <= leadlap )
                    texture.clear( imgFastest.getTexture(), offsetX + width*164/200, offsetY + height*70/100, false, null );
                else if( currentcarinfos.getFastestLaptime() == null || currentcarinfos.getLastLapTime() <= currentcarinfos.getFastestLaptime().getLapTime() )
                    texture.clear( imgFaster.getTexture(), offsetX + width*164/200, offsetY + height*70/100, false, null );
                else
                    texture.clear( imgSlower.getTexture(), offsetX + width*164/200, offsetY + height*70/100, false, null );
                 
                    
                texture.clear( imgPos.getTexture(), offsetX + width*86/100, offsetY, false, null );
                        
                break;
            default: 
                if(currentcarinfos.getSector() >= 2)
                {
                    if( cursec1.getValue() <= leadsec1 )
                        texture.clear( imgFastest.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                    else if( currentcarinfos.getFastestLaptime() == null || cursec1.getValue() <= currentcarinfos.getFastestLaptime().getSector1() )
                        texture.clear( imgFaster.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                    else
                        texture.clear( imgSlower.getTexture(), offsetX + width*148/200, offsetY + height*70/100, false, null );
                    
                }
                
                if(currentcarinfos.getSector() >= 3)
                {
                    if( cursec2.getValue() <= leadsec2 )
                        texture.clear( imgFastest.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                    else if( currentcarinfos.getFastestLaptime() == null || cursec2.getValue() <= currentcarinfos.getFastestLaptime().getSector2( true ) )
                        texture.clear( imgFaster.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                    else
                        texture.clear( imgSlower.getTexture(), offsetX + width*156/200, offsetY + height*70/100, false, null );
                     
                }
                break;
        }
    }
    
    private static final String getTimeAsGapString2( float gap )
    {
        if ( gap == 0f )
            return ( "-0.0000");// + String.valueOf( 0f ).substring( 0, 6 ) );
        
        if ( gap < 0f )
            return ( "-" + String.valueOf( -gap ).substring( 0, 6 ) );
        
        return ( "+" + String.valueOf( gap ).substring( 0, 6 ) );
    }
    private static final String getTimeAsString2( float time )
    {
        String rebuild;
        if(time >= 60)
            rebuild = String.valueOf( (int) time/60 ) + ":";
        else
            rebuild = "0:";
        
        if(time%60 >= 10)
            rebuild += String.valueOf(time%60);
        else
            rebuild += "0" + String.valueOf(time%60);
        
        if(rebuild.length() >= 9)
            return ( rebuild.substring( 0, 9 ) );
        else
        {
            for(int i=rebuild.length(); i < 9; i++)
            {
                rebuild += "0";
            }
            return ( rebuild );
        }
    }
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        updateSectorValues( scoringInfo );
        
        VehicleScoringInfo currentcarinfos = scoringInfo.getViewedVehicleScoringInfo();
        VehicleScoringInfo leadercarinfos = scoringInfo.getLeadersVehicleScoringInfo();
        
            
        leaderID.update( leadercarinfos.getDriverId() );
        leaderPos.update( leadercarinfos.getPlace( false ) );
        
        if ( needsCompleteRedraw || ( clock.c() && leaderID.hasChanged() ) )
        {
            dsTitle.draw( offsetX, offsetY, Title, texture );
            dsName.draw( offsetX, offsetY, ShortName( currentcarinfos.getDriverNameShort().toUpperCase()), texture );
            dsLeaderName.draw( offsetX, offsetY, ShortName( leadercarinfos.getDriverNameShort().toUpperCase()), texture );
        }
        
        
        
        ownPos.update( currentcarinfos.getPlace( false ) );
        
        switch ( situation.getValue() )
        {
            case LAST_SECONDS_OF_SECTOR_1:
                
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                {
                    dsPosST.draw( offsetX, offsetY, "ST", texture );
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                    
                }
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, getTimeAsString2( curlap.getValue() ) + "", texture);
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                {
                    if(ownPos.getValue() == 1)
                        dsPosTH.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTH.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTH.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTH.draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTH.draw( offsetX, offsetY, "TH", texture );
                    
                    dsCurrentPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                }
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadsec1), texture);
                }
                break;
                
            case SECTOR_1_FINISHED_BEGIN_SECTOR_2:
                gapOrTime.update( cursec1.getValue() - leadsec1 );
                
                if ( ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() )) && leadercarinfos.getFastestLaptime() != null )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), texture);
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                {
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                    dsPosST.draw( offsetX, offsetY, "ST", texture );
                }
                if ( needsCompleteRedraw || ( clock.c() && cursec1.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, getTimeAsString2( cursec1.getValue()) , texture);
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                {
                    dsCurrentPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                    if(ownPos.getValue() == 1)
                        dsPosTH.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTH.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTH.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTH.draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTH.draw( offsetX, offsetY, "TH", texture );
                }
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadsec1), texture);
                }
                break;
                
            case LAST_SECONDS_OF_SECTOR_2:
                gapOrTime.update( cursec1.getValue() - leadsec1 );
                
                if ( (needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() )) && leadercarinfos.getFastestLaptime() != null )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), texture);
                
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                {
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                    dsPosST.draw( offsetX, offsetY, "ST", texture );
                }
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, getTimeAsString2( curlap.getValue() ) + "", texture);
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                {
                    dsCurrentPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                    if(ownPos.getValue() == 1)
                        dsPosTH.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTH.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTH.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTH.draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTH.draw( offsetX, offsetY, "TH", texture );
                }
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadsec2), texture);
                }
                break;
                
            case SECTOR_2_FINISHED_BEGIN_SECTOR_3:
                gapOrTime.update( cursec2.getValue() - leadsec2 );
                
                if ( (needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() )) && leadercarinfos.getFastestLaptime() != null )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), texture);
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                {
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                    dsPosST.draw( offsetX, offsetY, "ST", texture );
                }
                if ( needsCompleteRedraw || ( clock.c() && cursec2.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, getTimeAsString2( cursec2.getValue() ) , texture);
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                {
                    dsCurrentPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                    if(ownPos.getValue() == 1)
                        dsPosTH.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTH.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTH.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTH.draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTH.draw( offsetX, offsetY, "TH", texture );
                }
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadsec2), texture);
                }
                break;
                
            case LAST_SECONDS_OF_SECTOR_LAP:
                gapOrTime.update( cursec2.getValue() - leadsec2 );
                
                if ( (needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() )) && leadercarinfos.getFastestLaptime() != null )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), texture);
                
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                {
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                    dsPosST.draw( offsetX, offsetY, "ST", texture );
                }
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, getTimeAsString2( curlap.getValue() ) + "", texture);
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                {
                    dsCurrentPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                    if(ownPos.getValue() == 1)
                        dsPosTH.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTH.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTH.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTH.draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTH.draw( offsetX, offsetY, "TH", texture );
                }
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadlap), texture);
                }
                break;
                
            case LAP_FINISHED_BEGIN_NEW_LAP:
                //remove
                if ( isEditorMode )
                {
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                    dsPosST.draw( offsetX, offsetY, "ST", texture );
                    dsCurrentPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                    if(ownPos.getValue() == 1)
                        dsPosTH.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTH.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTH.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTH.draw( offsetX, offsetY, "  TH", texture );
                    else
                        dsPosTH.draw( offsetX, offsetY, "TH", texture );
                }
                /////////////////
                float secondbest=0;
                oldbesttime.update( currentcarinfos.getBestLapTime() );
                
                if(oldbesttime.hasChanged())
                    oldbest = oldbesttime.getOldValue();
                
                if(ownPos.getValue() == 1)
                {
                    if(gameData.getScoringInfo().getSecondFastestLapVSI() != null && ownPos.getValue() != 1)
                        secondbest = gameData.getScoringInfo().getSecondFastestLapVSI().getBestLapTime(); 
                    else
                        secondbest = oldbest;
                }
                
                if (currentcarinfos.getLastLapTime() <= leadercarinfos.getBestLapTime() && secondbest < 0)
                    gapOrTime.update(  currentcarinfos.getLastLapTime() - oldbest );
                else
                    if( currentcarinfos.getLastLapTime() <= leadercarinfos.getBestLapTime() )
                        gapOrTime.update( currentcarinfos.getLastLapTime() - secondbest );
                    else
                        gapOrTime.update( currentcarinfos.getLastLapTime() - leadercarinfos.getBestLapTime() );
                    
                if ( needsCompleteRedraw || ( clock.c() && gapOrTime.hasChanged() ) )
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( gapOrTime.getValue() ), texture);
                
                
                
                lastLaptime.update( currentcarinfos.getLastLapTime() );
                
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                {
                    dsPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                    if(ownPos.getValue() == 1)
                        dsPosTHFinish.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTHFinish.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTHFinish.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTHFinish.draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTHFinish.draw( offsetX, offsetY, "TH", texture );
                }
                if ( needsCompleteRedraw || ( clock.c() && lastLaptime.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, getTimeAsString2( lastLaptime.getValue() ) , texture);
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadlap), texture);
                }
                break;
              
            case OTHER:
                // other cases not info not drawn
                
                if ( needsCompleteRedraw || ( clock.c() && leaderPos.hasChanged() ) )
                {
                    dsPosFrom.draw( offsetX, offsetY, leaderPos.getValueAsString(), texture );  
                    dsPosST.draw( offsetX, offsetY, "ST", texture );
                }
                
                
                if ( needsCompleteRedraw || ( clock.c() && curlap.hasChanged() ) )
                    dsTime.draw( offsetX, offsetY, getTimeAsString2( curlap.getValue() ) + "", texture);
                if ( needsCompleteRedraw || ( clock.c() && ownPos.hasChanged() ) )
                {
                    dsCurrentPos.draw( offsetX, offsetY, ownPos.getValueAsString(), texture );
                    if(ownPos.getValue() == 1)
                        dsPosTH.draw( offsetX, offsetY, "ST", texture );
                    else if(ownPos.getValue() == 2)
                        dsPosTH.draw( offsetX, offsetY, "ND", texture );
                    else if(ownPos.getValue() == 3)
                        dsPosTH.draw( offsetX, offsetY, "RD", texture );
                    else if(ownPos.getValue() >= 10)
                        dsPosTH.draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTH.draw( offsetX, offsetY, "TH", texture );
                }
                
                if(currentcarinfos.getSector() == 2  && leadercarinfos.getFastestLaptime() != null)
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( cursec1.getValue() - leadsec1 ), texture);
                else if(currentcarinfos.getSector() == 3 && leadercarinfos.getFastestLaptime() != null)
                    dsGap.draw( offsetX, offsetY, getTimeAsGapString2( cursec2.getValue() - leadsec2 ), texture);
                
                if(leadercarinfos.getFastestLaptime() != null)
                {
                    if(currentcarinfos.getSector() == 1)
                        dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadsec1), texture);
                    else if(currentcarinfos.getSector() == 2)
                        dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadsec2), texture);
                    else if(currentcarinfos.getSector() == 3)
                        dsLeaderTime.draw( offsetX, offsetY, getTimeAsString2(leadlap), texture);
                    
                }
                break;
        }
        
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( posFont, "" );
        writer.writeProperty( posFontTH, "" );
        writer.writeProperty( posFontTHBig, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( fontyoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( posFont ) );
        else if ( loader.loadProperty( posFontTH ) );
        else if ( loader.loadProperty( posFontTHBig ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( fontyoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        
        propsCont.addProperty( fontColor3 );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( posFont );
        propsCont.addProperty( posFontTH );
        propsCont.addProperty( posFontTHBig );
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
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
    
    public QualTimeWidget()
    {
        super( PrunnWidgetSetv8.INSTANCE, PrunnWidgetSetv8.WIDGET_PACKAGE_V8, 58.0f, 10.0f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetv8.V8_FONT_NAME );
    }
    
}
