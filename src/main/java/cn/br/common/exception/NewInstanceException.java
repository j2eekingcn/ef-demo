/**
 * Copyright (c) 2018, crossoverJie (crossoverJie@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.br.common.exception;

import lombok.NoArgsConstructor;

/**
 * New instance exception
 *
 * @author crossoverJie
 * @date 2018/10/8
 */
@NoArgsConstructor
public class NewInstanceException extends RuntimeException {

    public NewInstanceException(String message) {
        super(message);
    }

}