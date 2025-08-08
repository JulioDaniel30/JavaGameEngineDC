<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="GeneralSpritesheet" tilewidth="16" tileheight="16" tilecount="100" columns="10">
 <image source="spritesheet.png" width="160" height="160"/>
 <tile id="2">
  <properties>
   <property name="renderLayer" value="CHARACTERS"/>
   <property name="sprite_idle" value="player_idle"/>
   <property name="sprite_walk_right_1" value="player_walk_right_1&quot;"/>
   <property name="sprite_walk_right_2" value="player_walk_right_2&quot;"/>
   <property name="sprite_walk_right_3" value="player_walk_right_3&quot;"/>
  </properties>
 </tile>
 <tile id="6" type="lifepack"/>
 <tile id="17" type="enemy">
  <properties>
   <property name="renderLayer" value="CHARACTERS"/>
   <property name="speed" type="float" value="0.8"/>
   <property name="useAStar" type="bool" value="false"/>
   <property name="visionRadius" type="int" value="40"/>
  </properties>
 </tile>
 <tile id="22" type="door"/>
</tileset>
