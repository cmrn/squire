Squire - RPG Combat Assistant
=============================

Squire is a tool for pen & paper RPG players to track which buffs, debuffs, and other modifiers are affecting their characters stats at any given moment. The user can select which modifiers are currently active, and see the current value of each stat given those modifiers. The user can add, edit, and remove stats and modifiers.

How to build
------------
1. `git clone https://github.com/cmrn/squire.git`
2. `git submodule init`
3. `git submodule update`
4. Import the "Squire" project from the Squire folder into Eclipse
5. Import the "library" project from the ActionBarSherlock folder and rename it to "ActionBarSherlock"
6. Import the "library" project from the drag-sort-listview folder and rename it to "drag-sort-listview"
7. Replace `libs/android-support-v4.jar` in each of the library projects with the version from the Squire project
8. Build!

Bugs, Features, & Contributions
------------
If you find a bug or want to request a feature, please [raise an issue](https://github.com/cmrn/squire/issues). If you want to contribute code, please feel free to fork the project and submit a pull request.

License
-------
    Copyright 2012 Cameron Moon

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
