## TEST_BUILD v1.1.4

1. Added Draw by repetition (threefold repetition)
2. Added Draw by insufficient material
3. Minor tweaks

## TEST_BUILD v1.1.5

1. Added Draw by fifty-move rule
2. Added En Passant
3. Bug fixes

# v1.2.0

### GUI Overhaul
- The GUI has been made more colorful
- Update for the info-tab
- Note has been tucked away

### Command line
Added a command line input to the info-tab, you can use commands like:
* /flip - flip the chess board
* /coords - show coordinates / notation for squares
* /help - show dev's note
* /newgame - start a fresh game with default config
* /exit - quit the program
* /nexttheme, /prevtheme - cycle though GUI's color schemes
* /notation - (debug option) toggle between primitive coordinates and chess notation (/coords must be invoked first)

(You can press '/' to focus to the command input box)

Minor bug fixes and tweaks

# v1.3.0

### FEN
- Implemented FEN!, you can now generate FEN from a position or load a new game from FEN, more usage with commands

(FEN - Forsyth-Edwards Notation, a compact way to describe a particular board position in a chess game using alphanumeric characters. It includes information about the placement of pieces on the board, castling availability, en passant captures, and the current move number and player turn.)

### Info-tab and Console
The old info-tab has been split to 2 separate panel, the top info-tab now display info about the game, capture log and FEN generation if enabled, the console is now the main panel to handle command lines.

Update to command lines:
- Removed:
  * /help
  * /nexttheme, /prevtheme
- Added:
  * /clear : clear the console
  * /command : display all available commands
- Theme:
  * Color schemes have been cleaned up and make more polished, access them with /theme, available themes:
    * coral
    * chessdotcom
    * coffee
    * candy
    * metal
- Piece skin:
  * Added more piece sets, load them with /pieceskin, available skins:
    * cbrrunett
    * pixel
    * anarcandy

Minor update to GUI and bug fixes

## v1.3.1

### FEN Commands
Added FEN validation (I'm not sure if it's full proof) and a button to copy current FEN to clipboard

Overhauled commands relating to FEN:
- Toggle generation of FEN in info-tab: /gfen, /generatefen
- Load position from FEN: /lfen, /loadfen (+FEN)
- Validate FEN without changing the game: /vfen, /validatefen (+FEN)

I'm also working on more features and further stylize the info-tab, planned features:
- Timer
- Visual for capture log
- Real time notation log

## v1.3.2

- Improve FEN validation
- Fixed flipping board issue

## v1.3.3

- Fixed castling issues, 
- Update to FEN validator, now detonates no castling rights as '-'
- New commands:
  * /mute : toggle sounds
  * /help (+guide) : get additional information

# TEST_BOT v1.4.0

Added bot 'dummy', which makes move 100% randomly, you can try it out with new command:
- /bot (+bot) : play against bot as white
- /bot bot (+bot) (+delay_ms) : make bot play itself with specified delay in ms