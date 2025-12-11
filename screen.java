/* 
 Logic
  - Dynamically creates a 3×3 grid of video players.
  - Each screen loads a specified video URL with a timestamp.
  - Interaction features:
      • Hover = pause individual screen
      • Mouse leave = resume looping
      • Click = invert colors
      • Double-click = slow motion (0.3×)
  - Fscreen() acts as a small class-like constructor for screens.
*/
let pause_between_frames = 250; //used for image sequence mode 
let fscreens = [];              //Stores all screen objects 

// This is the Fscreen constructor: handles the video screens 
function Fscreen(filename, isLooping = false) {
  let container;
  
//if filename is an array, treat as animation frames 
  if (filename instanceof Array) {
    this.mode = "images";
    this.frames = filename;
    container = document.createElement("img");
    this.currentFrame = 0;

    //otherwise loads a video element 
  } else if (filename.includes("mp4") || filename.includes("webm")) {
    this.mode = "video";
    container = document.createElement("video");
    container.src = filename;
    container.loop = isLooping;
    container.autoplay = true;
    container.muted = true; // enable audio by removing this line
  }

  this.isLooping = isLooping;
  this.id = fscreens.length;
  container.id = "screen" + this.id;
  container.classList.add("fScreen");

  // Hover = pause
  container.addEventListener("mouseenter", () => {
    this.isLooping = false;
  });
  container.addEventListener("mouseleave", () => {
    this.isLooping = true;
  });

  // Click = invert colors
  container.addEventListener("click", () => {
    this.container.classList.toggle("invert");
  });

  // Double-click = toggle slow motion
  container.addEventListener("dblclick", () => {
    if (this.container.playbackRate === 1.0) {
      this.container.playbackRate = 0.3;
    } else {
      this.container.playbackRate = 1.0;
    }
  });

  const grid = document.getElementById("screens");
  if (grid) {
    grid.appendChild(container);
  } else {
    document.body.appendChild(container);
  }

  this.container = container;
}

//video sources 
document.addEventListener("DOMContentLoaded", () => {
  // Videos and their timestamps
  const videos = [
    "https://archive.org/download/04-vapormario-v-5-031021/04_vapormario_v5_031021.mp4?t=00:11:30",
    "https://archive.org/download/04-vapormario-v-5-031021/04_vapormario_v5_031021.mp4?t=00:14:51",
    "https://archive.org/download/abbadook_v4_js_020621/abbadook_v4_js_020621.mp4?t=00:11:52",
    "https://archive.org/download/abbadook_v4_js_020621/abbadook_v4_js_020621.mp4?t=00:02:21",
    "https://archive.org/download/sleepwavevol2_vidk_010822/sleepwavevol2_vidk_010822.mp4?t=00:00:40",
    "https://archive.org/download/sleepwavevol2_vidk_010822/sleepwavevol2_vidk_010822.mp4?t=00:09:30",
    "https://archive.org/download/05-rj-v-6c-2997-7mbps-aj-011721/05_r+j_v6c_2997_7mbps_aj011721.mp4?t=00:38:49",
    "https://archive.org/download/05-rj-v-6c-2997-7mbps-aj-011721/05_r+j_v6c_2997_7mbps_aj011721.mp4?t=01:20:00",
    "https://archive.org/download/beachhouse_wholething_v3_aj_042121/beachhouse_wholething_v3_aj_042121.mp4?t=00:48:35"
  ];

  // Create 9 screens
  for (let i = 0; i < 9; i++) {
    let src = i < videos.length ? videos[i] : videos[0]; // fill remaining slots if needed
    let screen = new Fscreen(src, true);
    screen.container.playbackRate = 1.0;
    fscreens.push(screen);
  }

  animate();
// Main animation loop:
// - Resumes or pauses videos depending on hover state
// - Advances image frame sequences if used
// User's manual
  let usersManual = document.createElement("p");
  usersManual.id = "usersManual";
  usersManual.innerText =
    "USER INSTRUCTIONS —\n" +
    "• Hover over a screen: that clip pauses.\n" +
    "• Move mouse away: all screens resume looping.\n" +
    "• Click a screen: invert its colors for contrast.\n" +
    "• Double‑click a screen: toggle slow‑motion playback (≈ 30% speed).\n" ;
  document.body.appendChild(usersManual);
});

async function animate() {
  for (let fs of fscreens) {
    if (fs.mode === "video") {
      if (fs.isLooping && fs.container.paused) {
        fs.container.play();
      }
      if (!fs.isLooping) {
        fs.container.pause();
      }
    } else if (fs.mode === "images") {
      if (fs.isLooping) {
        fs.currentFrame = (fs.currentFrame + 1) % fs.frames.length;
        fs.container.src = fs.frames[fs.currentFrame];
      }
    }
  }
  await sleep();  // Simple async sleep helper used for frame delays
  animate();
}

async function sleep(ms = pause_between_frames) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
