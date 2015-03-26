/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.security.pwdgen.md5;

class MD5State
{
    /**
     * 128-bit state
     */
    int state[];

    /**
     * 64-bit character count
     */
    long count;

    /**
     * 64-byte buffer (512 bits) for storing to-be-hashed characters
     */
    byte buffer[];

    public MD5State()
    {
        buffer = new byte[64];
        count = 0;
        state = new int[4];

        state[0] = 0x67452301;
        state[1] = 0xefcdab89;
        state[2] = 0x98badcfe;
        state[3] = 0x10325476;
    }

    /**
     * Create this State as a copy of another state
     */
    public MD5State(MD5State from)
    {
        this();

        int i;

        for (i = 0; i < buffer.length; i++) {
            this.buffer[i] = from.buffer[i];
        }

        for (i = 0; i < state.length; i++) {
            this.state[i] = from.state[i];
        }

        this.count = from.count;
    }
};

