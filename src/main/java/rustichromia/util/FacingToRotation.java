package rustichromia.util;

/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Quaternion;

public enum FacingToRotation
{

    // DUNSWE
    // @formatter:off
    DOWN_DOWN( new Rotation( 0, 0, 0 ) ), // NOOP
    DOWN_UP( new Rotation( 0, 0, 0 ) ), // NOOP
    DOWN_NORTH( new Rotation( -90, 0, 0 ) ),
    DOWN_SOUTH( new Rotation( -90, 0, 180 ) ),
    DOWN_WEST( new Rotation( -90, 0, 90 ) ),
    DOWN_EAST( new Rotation( -90, 0, -90 ) ),
    UP_DOWN( new Rotation( 0, 0, 0 ) ), // NOOP
    UP_UP( new Rotation( 0, 0, 0 ) ), // NOOP
    UP_NORTH( new Rotation( 90, 0, 180 ) ),
    UP_SOUTH( new Rotation( 90, 0, 0 ) ),
    UP_WEST( new Rotation( 90, 0, 90 ) ),
    UP_EAST( new Rotation( 90, 0, -90 ) ),
    NORTH_DOWN( new Rotation( 0, 0, 180 ) ),
    NORTH_UP( new Rotation( 0, 0, 0 ) ),
    NORTH_NORTH( new Rotation( 0, 0, 0 ) ), // NOOP
    NORTH_SOUTH( new Rotation( 0, 0, 0 ) ), // NOOP
    NORTH_WEST( new Rotation( 0, 0, 90 ) ),
    NORTH_EAST( new Rotation( 0, 0, -90 ) ),
    SOUTH_DOWN( new Rotation( 0, 180, 180 ) ),
    SOUTH_UP( new Rotation( 0, 180, 0 ) ),
    SOUTH_NORTH( new Rotation( 0, 0, 0 ) ), // NOOP
    SOUTH_SOUTH( new Rotation( 0, 0, 0 ) ), // NOOP
    SOUTH_WEST( new Rotation( 0, 180, -90 ) ),
    SOUTH_EAST( new Rotation( 0, 180, 90 ) ),
    WEST_DOWN( new Rotation( 0, 90, 180 ) ),
    WEST_UP( new Rotation( 0, 90, 0 ) ),
    WEST_NORTH( new Rotation( 0, 90, -90 ) ),
    WEST_SOUTH( new Rotation( 0, 90, 90 ) ),
    WEST_WEST( new Rotation( 0, 0, 0 ) ), // NOOP
    WEST_EAST( new Rotation( 0, 0, 0 ) ), // NOOP
    EAST_DOWN( new Rotation( 0, -90, 180 ) ),
    EAST_UP( new Rotation( 0, -90, 0 ) ),
    EAST_NORTH( new Rotation( 0, -90, 90 ) ),
    EAST_SOUTH( new Rotation( 0, -90, -90 ) ),
    EAST_WEST( new Rotation( 0, 0, 0 ) ), // NOOP
    EAST_EAST( new Rotation( 0, 0, 0 ) ); // NOOP
    // @formatter:on

    private final Rotation rot;
    private final Quaternion quat;

    FacingToRotation(Rotation rot)
    {
        this.rot = rot;
        this.quat = rot.toQuaternion();
    }

    public Rotation getRot()
    {
        return rot;
    }

    public Quaternion getQuat() {
        return quat;
    }

    public static FacingToRotation get(EnumFacing forward, EnumFacing up )
    {
        return values()[forward.ordinal() * 6 + up.ordinal()];
    }

}
