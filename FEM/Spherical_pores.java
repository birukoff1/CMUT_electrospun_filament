POROSITY = POROSITY/100.0;
int ind = 0;
double hx, hy, hz, hr = 0.0;

double CUBE_LENGTH = 10.0;
model.component("comp1").geom("geom1").lengthUnit("mm");

model.component("comp1").geom("geom1").selection().remove("csel1");
model.component("comp1").geom("geom1").selection().create("csel1", "CumulativeSelection");

String[] feats = model.component("comp1").geom("geom1").feature().tags();
for (String tag : feats) {
  if (tag.startsWith("sph")) {
    model.component("comp1").geom("geom1").feature().remove(tag);
  }
}

double V = CUBE_LENGTH*CUBE_LENGTH*CUBE_LENGTH;

int[] candidates = {10, 18, 25, 49, 81, 121, 225, 625, 1225, 3000};

for (int N : candidates) {
  int N_high = (int) Math.cbrt(N); // Number of pores at outside structure
  int N_low = N_high-1; // Number of pores at inside structure
  
  double PORE_RADIUS = Math.cbrt(POROSITY*V/(4.0/3.0*Math.PI*(N_high*N_high*N_high+N_low*N_low*N_low)));
  double PORE_MIN_RADIUS = PORE_RADIUS*0.9; // Slight variation in size of the pores
  double PORE_MAX_RADIUS = PORE_RADIUS*1.1;
  
  double d = 2.2*PORE_RADIUS*Math.sqrt(2); // Distance between the pores for uniform distribution (2.2 - theoretical)
  
  if (d*(N_high-1)+2*PORE_RADIUS+PORE_RADIUS/2 > CUBE_LENGTH) { // Check if this number of pores suits
    continue;
    
  } else {
    
    double Shift = (CUBE_LENGTH-d*(N_high-1)-2*PORE_RADIUS)/2;
    
    for (int x = 0; x < N_high; x++) {
      for (int y = 0; y < N_high; y++) {
        for (int z = 0; z < N_high; z++) {
          hx = Shift+PORE_RADIUS+d*x;
          hy = Shift+PORE_RADIUS+d*y;
          hz = Shift+PORE_RADIUS+d*z;
          hr = Math.random()*(PORE_MAX_RADIUS-PORE_MIN_RADIUS)+PORE_MIN_RADIUS;
          model.component("comp1").geom("geom1").create("sph"+ind, "Sphere");
          model.component("comp1").geom("geom1").feature("sph"+ind).set("r", hr);
          model.component("comp1").geom("geom1").feature("sph"+ind).set("pos", new double[]{hx, hy, hz});
          model.component("comp1").geom("geom1").feature("sph"+ind).set("contributeto", "csel1");
          ind++;
        }
      }
    }
    
    for (int x = 0; x < N_low; x++) {
      for (int y = 0; y < N_low; y++) {
        for (int z = 0; z < N_low; z++) {
          hx = Shift+PORE_RADIUS+d/2+d*x;
          hy = Shift+PORE_RADIUS+d/2+d*y;
          hz = Shift+PORE_RADIUS+d/2+d*z;
          hr = Math.random()*(PORE_MAX_RADIUS-PORE_MIN_RADIUS)+PORE_MIN_RADIUS;
          model.component("comp1").geom("geom1").create("sph"+ind, "Sphere");
          model.component("comp1").geom("geom1").feature("sph"+ind).set("r", hr);
          model.component("comp1").geom("geom1").feature("sph"+ind).set("pos", new double[]{hx, hy, hz});
          model.component("comp1").geom("geom1").feature("sph"+ind).set("contributeto", "csel1");
          ind++;
        }
      }
    }
    break;
  }
}

model.component("comp1").geom("geom1").run();
model.component("comp1").geom("geom1").run("fin");