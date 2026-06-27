import csv
import uuid
import datetime

# English Templates Configuration
templates_en = [
    {
        "id": "e81d77a0-0001-4c12-a167-9bf461d3fa01",
        "title": "Class 11 Physics - Mechanics Mastery (English)",
        "description": "Master Kinematics, Laws of Motion, Work, Energy & Power in 20 days with daily tasks and MCQ drills.",
        "category": "Physics Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0002-4c12-a167-9bf461d3fa02",
        "title": "Class 12 Physics - Electrostatics & Magnetism (English)",
        "description": "Clear conceptual doubts in Coulomb's Law, Electric Fields, Gauss Law, and Magnetic fields.",
        "category": "Physics Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0003-4c12-a167-9bf461d3fa03",
        "title": "Class 11 Chemistry - Physical Chemistry Basics (English)",
        "description": "Understand Mole Concept, Atomic Structure, and Chemical Bonding step-by-step.",
        "category": "Chemistry Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0004-4c12-a167-9bf461d3fa04",
        "title": "Class 12 Chemistry - Organic Reactions (English)",
        "description": "Learn Named Reactions, Mechanisms of Haloalkanes, Alcohols, Ethers, and Aldehydes.",
        "category": "Chemistry Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0005-4c12-a167-9bf461d3fa05",
        "title": "Class 11 Mathematics - Trigonometry & Algebra (English)",
        "description": "Boost your speed in Trigonometric Functions, Complex Numbers, and Permutations.",
        "category": "Mathematics Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0006-4c12-a167-9bf461d3fa06",
        "title": "Class 12 Mathematics - Calculus Booster (English)",
        "description": "Master Limits, Continuity, Differentiation, and Integral Calculus for CBSE/JEE.",
        "category": "Mathematics Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0007-4c12-a167-9bf461d3fa07",
        "title": "Class 11 Physics - Waves & Thermodynamics (English)",
        "description": "Understand Heat Transfer, Kinetic Theory of Gases, Oscillations, and Wave Propagation.",
        "category": "Physics Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0008-4c12-a167-9bf461d3fa08",
        "title": "Class 12 Chemistry - Electrochemistry & Kinetics (English)",
        "description": "Nernst Equation, Conductance, Rate Laws, and Activation Energy made easy.",
        "category": "Chemistry Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0009-4c12-a167-9bf461d3fa09",
        "title": "Class 12 Mathematics - Vectors & 3D Geometry (English)",
        "description": "Master Dot/Cross Product, Lines, Planes, and Direction Cosines in 3D Space.",
        "category": "Mathematics Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0010-4c12-a167-9bf461d3fa10",
        "title": "Class 11 Mathematics - Coordinate Geometry (English)",
        "description": "A comprehensive study of Straight Lines, Circles, Parabola, Ellipse, and Hyperbola.",
        "category": "Mathematics Class 11",
        "days": 20
    }
]

# Hindi Templates Configuration
templates_hi = [
    {
        "id": "e81d77a0-0101-4c12-a167-9bf461d3fa01",
        "title": "कक्षा 11 भौतिक विज्ञान - मैकेनिक्स मास्टरी (हिंदी)",
        "description": "20 दिनों में किनेमैटिक्स, न्यूटन के गति के नियम, कार्य, ऊर्जा और शक्ति को दैनिक कार्यों और MCQ अभ्यास के साथ सीखें।",
        "category": "Physics Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0102-4c12-a167-9bf461d3fa02",
        "title": "कक्षा 12 भौतिक विज्ञान - इलेक्ट्रोस्टेटिक्स और चुंबकत्व (हिंदी)",
        "description": "कूलॉम का नियम, विद्युत क्षेत्र, गॉस का नियम और चुंबकीय क्षेत्रों की अवधारणाओं को सरल शब्दों में समझें।",
        "category": "Physics Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0103-4c12-a167-9bf461d3fa03",
        "title": "कक्षा 11 रसायन विज्ञान - भौतिक रसायन विज्ञान के मूल सिद्धांत (हिंदी)",
        "description": "मोल अवधारणा, परमाणु संरचना और रासायनिक आबंधन को चरण-दर-चरण स्पष्ट करें।",
        "category": "Chemistry Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0104-4c12-a167-9bf461d3fa04",
        "title": "कक्षा 12 रसायन विज्ञान - कार्बनिक अभिक्रियाएं (हिंदी)",
        "description": "नाम वाली रासायनिक अभिक्रियाएं, हैलोएल्केन, अल्कोहल, ईथर और एल्डिहाइड के तंत्र को जानें।",
        "category": "Chemistry Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0105-4c12-a167-9bf461d3fa05",
        "title": "कक्षा 11 गणित - त्रिकोणमिति और बीजगणित (हिंदी)",
        "description": "त्रिकोणमितीय फलन, सम्मिश्र संख्याएं और क्रमचय-संचय में अपनी गति और कौशल को बढ़ाएं।",
        "category": "Mathematics Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0106-4c12-a167-9bf461d3fa06",
        "title": "कक्षा 12 गणित - कैलकुलस बूस्टर (हिंदी)",
        "description": "CBSE/JEE स्तर के लिए सीमा, सततता, अवकलन और समाकलन में महारत हासिल करें।",
        "category": "Mathematics Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0107-4c12-a167-9bf461d3fa07",
        "title": "कक्षा 11 भौतिक विज्ञान - तरंगें और ऊष्मागतिकी (हिंदी)",
        "description": "ऊष्मा स्थानांतरण, गैसों का अणुगति सिद्धांत, दोलन और तरंग संचरण को समझें।",
        "category": "Physics Class 11",
        "days": 20
    },
    {
        "id": "e81d77a0-0108-4c12-a167-9bf461d3fa08",
        "title": "कक्षा 12 रसायन विज्ञान - विद्युत रसायन और गतिकी (हिंदी)",
        "description": "नेर्नस्ट समीकरण, चालकता, दर नियम और सक्रियण ऊर्जा को आसान अवधारणाओं के साथ सीखें।",
        "category": "Chemistry Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0109-4c12-a167-9bf461d3fa09",
        "title": "कक्षा 12 गणित - सदिश और त्रिविमीय ज्यामिति (हिंदी)",
        "description": "3D स्पेस में डॉट/क्रॉस गुणनफल, रेखाएं, समतल और दिक्-कोज्याओं को समझें।",
        "category": "Mathematics Class 12",
        "days": 20
    },
    {
        "id": "e81d77a0-0110-4c12-a167-9bf461d3fa10",
        "title": "कक्षा 11 गणित - निर्देशांक ज्यामिति (हिंदी)",
        "description": "सरल रेखाएं, वृत्त, परवलय, दीर्घवृत्त और अतिपरवलय का संपूर्ण अध्ययन।",
        "category": "Mathematics Class 11",
        "days": 20
    }
]

pcm_questions_en = {
    "Physics Class 11": [
        ("What is the SI unit of power?", "Watt", "Joule", "Newton", "Pascal", "A", "Power is work done per unit time. Unit is Watt (J/s)."),
        ("What is the acceleration due to gravity on Earth's surface?", "9.8 m/s^2", "8.9 m/s^2", "10.5 m/s^2", "7.6 m/s^2", "A", "Standard value of g is 9.8 m/s^2."),
        ("A particle moves in a circle of radius r with constant speed v. What is its centripetal acceleration?", "v^2/r", "v/r", "v^2 r", "v r^2", "A", "Centripetal acceleration is given by a = v^2/r."),
        ("What type of collision conserves both momentum and kinetic energy?", "Elastic", "Inelastic", "Perfect Inelastic", "Plastic", "A", "Elastic collisions conserve both kinetic energy and linear momentum."),
        ("Work done by a centripetal force is always:", "Zero", "Positive", "Negative", "Infinite", "A", "Force and displacement are perpendicular (theta = 90), so Work = F.d.cos(90) = 0."),
        ("The area under a velocity-time graph represents:", "Displacement", "Acceleration", "Force", "Impulse", "A", "Integral of v dt represents displacement."),
        ("The moment of inertia of a ring of mass M and radius R about its central axis is:", "MR^2", "1/2 MR^2", "2/5 MR^2", "3/2 MR^2", "A", "I = MR^2 for a thin circular ring about its axis."),
        ("Escape velocity of a body from Earth's surface is approximately:", "11.2 km/s", "8.2 km/s", "5.6 km/s", "11.2 m/s", "A", "Ve = sqrt(2gRe) approx 11.2 km/s."),
        ("According to Hooke's law, stress is proportional to:", "Strain", "Elastic limit", "Force", "Young's Modulus", "A", "Stress = E * Strain within elastic limits."),
        ("Viscosity of a liquid generally _____ with temperature increase.", "Decreases", "Increases", "Remains constant", "First increases then decreases", "A", "Liquid viscosity decreases with higher temperature due to weaker cohesive forces.")
    ],
    "Physics Class 12": [
        ("The force between two point charges is governed by:", "Coulomb's Law", "Ohm's Law", "Ampere's Law", "Faraday's Law", "A", "F = k*q1*q2/r^2 is Coulomb's Law."),
        ("What is the SI unit of capacitance?", "Farad", "Henry", "Tesla", "Ohm", "A", "Capacitance C = Q/V. Unit is Farad (F)."),
        ("The electric field inside a perfectly conducting sphere is:", "Zero", "Maximum", "Infinite", "Depends on charge", "A", "Charges reside on the outer surface of a conductor, so interior field is zero."),
        ("Drift velocity of electrons is directly proportional to:", "Electric field", "Length of conductor", "Area of cross-section", "Resistance", "A", "vd = e*E*tau/m, so vd is proportional to E."),
        ("The magnetic force on a charge q moving with velocity v in field B is:", "q(v x B)", "q(v . B)", "v(q x B)", "B(q x v)", "A", "F = q * (v x B)."),
        ("What is the relation between power factor and phase angle phi?", "cos(phi)", "sin(phi)", "tan(phi)", "cot(phi)", "A", "Power factor is defined as cos(phi) = R/Z."),
        ("Which electromagnetic wave has the shortest wavelength?", "Gamma rays", "X-rays", "UV rays", "Radio waves", "A", "Gamma rays have highest frequency and shortest wavelength in the EM spectrum."),
        ("A convex lens behaves as a diverging lens when placed in a medium of refractive index:", "Greater than lens index", "Less than lens index", "Equal to lens index", "Zero", "A", "Focal length changes sign if medium refractive index is greater than lens index."),
        ("The threshold frequency depends on:", "Nature of metal surface", "Intensity of light", "Frequency of light", "Velocity of electrons", "A", "Work function and threshold frequency are characteristic properties of the metal."),
        ("In a p-type semiconductor, the majority charge carriers are:", "Holes", "Electrons", "Protons", "Neutrons", "A", "P-type has trivalent impurities, creating excess holes.")
    ],
    "Chemistry Class 11": [
        ("What is the mass of 1 mole of Carbon-12 atoms?", "12 g", "1.2 g", "120 g", "6 g", "A", "1 mole of C-12 weighs exactly 12 grams."),
        ("The principal quantum number (n) describes:", "Size of orbital", "Shape of orbital", "Orientation of orbital", "Spin of electron", "A", "Principal quantum number indicates energy level and size of orbital."),
        ("Which element has the highest electronegativity?", "Fluorine", "Oxygen", "Chlorine", "Nitrogen", "A", "Fluorine is the most electronegative element in periodic table (value = 4.0)."),
        ("Which geometry is associated with sp3 hybridization?", "Tetrahedral", "Trigonal planar", "Linear", "Octahedral", "A", "sp3 hybridization results in tetrahedral geometry with 109.5 degree angles."),
        ("An ideal gas obeys the equation:", "PV = nRT", "PV = RT", "P/V = RT", "PV^2 = nRT", "A", "Ideal gas law is PV = nRT."),
        ("For a spontaneous process, the change in Gibbs free energy (dG) must be:", "Negative", "Positive", "Zero", "Positive or Negative", "A", "dG < 0 is the condition for spontaneity at constant T and P."),
        ("What is the conjugate base of HSO4-?", "SO4^2-", "H2SO4", "H+", "OH-", "A", "Conjugate base is formed by removing one H+ proton, yielding SO4^2-."),
        ("Oxidation number of Hydrogen in metal hydrides (like NaH) is:", "-1", "+1", "0", "+2", "A", "Metals are more electropositive, so Hydrogen acts as hydride with -1 oxidation state."),
        ("Heavy water is chemically:", "Deuterium oxide (D2O)", "Tritium oxide (T2O)", "Hydrogen peroxide (H2O2)", "Distilled water", "A", "Heavy water contains deuterium isotope instead of protium."),
        ("Which alkali metal is the strongest reducing agent in aqueous solution?", "Lithium", "Sodium", "Potassium", "Cesium", "A", "Lithium has the highest negative electrode potential due to high hydration energy.")
    ],
    "Chemistry Class 12": [
        ("The number of atoms in a body-centered cubic (BCC) unit cell is:", "2", "1", "4", "6", "A", "BCC cell has 8 corners * 1/8 + 1 center * 1 = 2 atoms."),
        ("Relative lowering of vapor pressure is equal to:", "Mole fraction of solute", "Molarity of solute", "Mole fraction of solvent", "Molality", "A", "According to Raoult's law, dP/P0 = X_solute."),
        ("Nernst equation relates electrode potential with:", "Concentration of ions", "Pressure", "Viscosity", "Volume", "A", "E = E0 - (RT/nF) * ln(Q), relating potential with concentration."),
        ("What is the order of reaction if unit of rate constant is s^-1?", "First order", "Zero order", "Second order", "Third order", "A", "Rate = k[A]^1 => k = Rate/[A] = (M/s)/M = s^-1."),
        ("Which of the following is a lyophilic colloid?", "Gum", "Gold sol", "Sulphur sol", "Fe(OH)3 sol", "A", "Organic substances like gum, gelatin, starch form lyophilic sols."),
        ("In the extraction of Iron, the flux used is:", "Limestone", "Silica", "Coke", "Alumina", "A", "Limestone (CaCO3) decomposes to CaO which acts as basic flux to remove SiO2 slag."),
        ("Which noble gas is most reactive?", "Xenon", "Helium", "Neon", "Argon", "A", "Xenon has lower ionization enthalpy and forms compounds like XeF2, XeF4, XeO3."),
        ("The hybridization of Ni in [Ni(CN)4]^2- is:", "dsp2", "sp3", "d2sp3", "sp3d2", "A", "CN- is a strong field ligand, forcing pairing, resulting in square planar dsp2 geometry."),
        ("SN1 reaction occurs faster in:", "Tertiary alkyl halides", "Primary alkyl halides", "Methyl halides", "Secondary alkyl halides", "A", "SN1 involves carbocation intermediate, tertiary carbocations are most stable."),
        ("Which of the following carbohydrates is a disaccharide?", "Sucrose", "Glucose", "Fructose", "Starch", "A", "Sucrose hydrolyzes into Glucose and Fructose. It is a disaccharide.")
    ],
    "Mathematics Class 11": [
        ("If set A has n elements, the number of subsets of A is:", "2^n", "n^2", "2n", "n!", "A", "Power set cardinality is 2^n."),
        ("What is the value of cos(pi)?", "-1", "1", "0", "1/2", "A", "cos(180 degrees) is -1."),
        ("The value of i^4 is:", "1", "-1", "i", "-i", "A", "i^2 = -1, so i^4 = (-1)^2 = 1."),
        ("The number of ways to arrange n distinct objects in a circle is:", "(n-1)!", "n!", "n^2", "(n+1)!", "A", "Circular permutations formula is (n-1)!."),
        ("What is the sum of infinite terms of a GP: a, ar, ar^2... (|r|<1)?", "a / (1 - r)", "a / (1 + r)", "a(1-r)", "a/(r-1)", "A", "S_inf = a / (1 - r)."),
        ("The distance between points (x1, y1) and (x2, y2) is given by:", "sqrt((x2-x1)^2 + (y2-y1)^2)", "(x2-x1)^2 + (y2-y1)^2", "x2-x1 + y2-y1", "sqrt(x1^2 + y1^2)", "A", "Standard Euclidean distance formula."),
        ("What is the slope of a line parallel to the y-axis?", "Undefined", "0", "1", "-1", "A", "Slope of vertical line is tan(90) which is undefined."),
        ("The equation x^2/a^2 - y^2/b^2 = 1 represents a:", "Hyperbola", "Ellipse", "Parabola", "Circle", "A", "Minus sign denotes hyperbola; plus sign denotes ellipse."),
        ("What is the value of lim_{x->0} (sin x / x)?", "1", "0", "Undefined", "Infinity", "A", "Standard trigonometric limit is 1."),
        ("If two events are mutually exclusive, then P(A cap B) is:", "0", "1", "P(A)*P(B)", "P(A)+P(B)", "A", "Mutually exclusive means they cannot happen together; intersection is empty set.")
    ],
    "Mathematics Class 12": [
        ("If f(x) = x^2, what is f'(x)?", "2x", "x", "2", "3x^2", "A", "Derivative of x^n is n*x^(n-1)."),
        ("What is the integral of 1/x dx?", "ln|x| + C", "-1/x^2 + C", "e^x + C", "x^2/2 + C", "A", "Integral of 1/x is natural log of absolute value of x plus constant C."),
        ("The order of the differential equation (dy/dx)^3 + d^2y/dx^2 = 0 is:", "2", "3", "1", "0", "A", "Order is the highest derivative present (which is 2)."),
        ("If vector A = i + j and vector B = i - j, what is their dot product?", "0", "2", "-2", "1", "A", "A.B = (1*1) + (1*-1) = 1 - 1 = 0. They are perpendicular."),
        ("The direction cosines of a line parallel to x-axis are:", "1, 0, 0", "0, 1, 0", "0, 0, 1", "1, 1, 1", "A", "Angle with x-axis is 0, others are 90. cos(0)=1, cos(90)=0."),
        ("What is the value of a determinant of a matrix with two identical rows?", "0", "1", "Infinite", "Cannot be determined", "A", "Property of determinants: identical rows lead to value of 0."),
        ("If A is a square matrix, then A + A^T is always:", "Symmetric", "Skew-symmetric", "Diagonal", "Identity", "A", "(A+AT)^T = A^T + (A^T)^T = A^T + A = A + A^T. Hence symmetric."),
        ("What is the derivative of e^{3x}?", "3e^{3x}", "e^{3x}", "3x e^{3x}", "1/3 e^{3x}", "A", "Using Chain Rule: d/dx(e^u) = e^u * du/dx."),
        ("Integral of cos(x) dx is:", "sin(x) + C", "-sin(x) + C", "tan(x) + C", "cos(x) + C", "A", "Integral of cos(x) is sin(x) + C."),
        ("The probability of drawing a red card from a standard deck is:", "1/2", "1/4", "1/13", "2/13", "A", "26 red cards out of 52 gives 26/52 = 1/2.")
    ]
}

pcm_questions_hi = {
    "Physics Class 11": [
        ("शक्ति का SI मात्रक क्या है?", "वाट", "जूल", "न्यूटन", "पास्कल", "A", "शक्ति कार्य करने की दर है। इसका मात्रक वाट (J/s) है।"),
        ("पृथ्वी की सतह पर गुरुत्वाकर्षण के कारण त्वरण का मान क्या है?", "9.8 मी/से^2", "8.9 मी/से^2", "10.5 मी/से^2", "7.6 मी/से^2", "A", "g का मानक मान 9.8 मी/से^2 है।"),
        ("एक कण त्रिज्या r के वृत्त में नियत चाल v से घूमता है। इसका अभिकेंद्री त्वरण क्या होगा?", "v^2/r", "v/r", "v^2 r", "v r^2", "A", "अभिकेंद्री त्वरण का सूत्र a = v^2/r होता है।"),
        ("किस प्रकार की टक्कर में संवेग और गतिज ऊर्जा दोनों संरक्षित रहते हैं?", "प्रत्यास्थ (Elastic)", "अप्रत्यास्थ (Inelastic)", "पूर्णतः अप्रत्यास्थ", "प्लास्टिक", "A", "प्रत्यास्थ टक्कर में गतिज ऊर्जा और रेखीय संवेग दोनों संरक्षित रहते हैं।"),
        ("अभिकेंद्री बल द्वारा किया गया कार्य हमेशा होता है:", "शून्य", "धनात्मक", "ऋणात्मक", "अनंत", "A", "बल और विस्थापन परस्पर लंबवत होते हैं (theta = 90), इसलिए कार्य = F.d.cos(90) = 0।"),
        ("वेग-समय ग्राफ के अंतर्गत क्षेत्र क्या दर्शाता है?", "विस्थापन", "त्वरण", "बल", "आवेग", "A", "वेग के समाकलन (v dt) से विस्थापन प्राप्त होता है।"),
        ("द्रव्यमान M और त्रिज्या R के एक वलय (Ring) का उसके केंद्रीय अक्ष के परितः जड़त्व आघूर्ण है:", "MR^2", "1/2 MR^2", "2/5 MR^2", "3/2 MR^2", "A", "वलय के अक्ष के परितः जड़त्व आघूर्ण I = MR^2 होता है।"),
        ("पृथ्वी की सतह से किसी पिंड का पलायन वेग लगभग कितना है?", "11.2 किमी/सेकंड", "8.2 किमी/सेकंड", "5.6 किमी/सेकंड", "11.2 मीटर/सेकंड", "A", "पलायन वेग Ve = sqrt(2gRe) लगभग 11.2 किमी/सेकंड होता है।"),
        ("हुक के नियम के अनुसार, प्रतिबल किसके समानुपाती होता है?", "विकृति (Strain)", "प्रत्यास्थता सीमा", "बल", "यंग मापांक", "A", "प्रत्यास्थता सीमा के भीतर प्रतिबल = E * विकृति।"),
        ("तापमान बढ़ने पर तरल की श्यानता (Viscosity) आमतौर पर:", "घटती है", "बढ़ती है", "समान रहती है", "पहले बढ़ती है फिर घटती है", "A", "ससंजक बल कमजोर होने के कारण तापमान बढ़ने पर द्रवों की श्यानता घटती है।")
    ],
    "Physics Class 12": [
        ("दो बिंदु आवेशों के बीच का बल किस नियम से नियंत्रित होता है?", "कूलॉम का नियम", "ओम का नियम", "एम्पीयर का नियम", "फैराडे का नियम", "A", "F = k*q1*q2/r^2 कूलॉम का नियम है।"),
        ("धारिता (Capacitance) का SI मात्रक क्या है?", "फैरड", "हेनरी", "टेस्ला", "ओम", "A", "धारिता C = Q/V। इसका मात्रक फैरड (F) है।"),
        ("एक आदर्श चालक गोले के भीतर विद्युत क्षेत्र होता है:", "शून्य", "अधिकतम", "अनंत", "आवेश पर निर्भर", "A", "चालक के भीतर कोई अतिरिक्त आवेश नहीं रहता, अतः विद्युत क्षेत्र शून्य होता है।"),
        ("इलेक्ट्रॉनों का अपवाह वेग (Drift velocity) किसके सीधे समानुपाती होता है?", "विद्युत क्षेत्र", "चालक की लंबाई", "अनुप्रस्थ काट का क्षेत्रफल", "प्रतिरोध", "A", "vd = e*E*tau/m, इसलिए vd विद्युत क्षेत्र E के समानुपाती है।"),
        ("चुंबकीय क्षेत्र B में वेग v से गतिमान आवेश q पर चुंबकीय बल होता है:", "q(v x B)", "q(v . B)", "v(q x B)", "B(q x v)", "A", "बल F = q * (v x B) होता है।"),
        ("शक्ति गुणांक (Power factor) और कला कोण phi के बीच क्या संबंध है?", "cos(phi)", "sin(phi)", "tan(phi)", "cot(phi)", "A", "शक्ति गुणांक cos(phi) = R/Z के रूप में परिभाषित है।"),
        ("किस विद्युत चुंबकीय तरंग की तरंगदैर्ध्य सबसे कम होती है?", "गामा किरणें", "एक्स-किरणें", "पराबैंगनी किरणें", "रेडियो तरंगें", "A", "गामा किरणों की आवृत्ति सबसे अधिक और तरंगदैर्ध्य सबसे कम होती है।"),
        ("एक उत्तल लेंस अपसारी लेंस की तरह व्यवहार करता है जब उसे किस अपवर्तनांक के माध्यम में रखा जाए?", "लेंस के अपवर्तनांक से अधिक", "लेंस के अपवर्तनांक से कम", "लेंस के अपवर्तनांक के बराबर", "शून्य", "A", "यदि माध्यम का अपवर्तनांक लेंस से अधिक हो तो फोकस दूरी का चिह्न बदल जाता है।"),
        ("देहली आवृत्ति (Threshold frequency) निर्भर करती है:", "धातु की सतह की प्रकृति पर", "प्रकाश की तीव्रता पर", "प्रकाश की आवृत्ति पर", "इलेक्ट्रॉन के वेग पर", "A", "कार्य फलन और देहली आवृत्ति धातु के विशिष्ट गुण हैं।"),
        ("p-प्रकार के अर्धचालक में बहुसंख्यक आवेश वाहक होते हैं:", "कोटर (Holes)", "इलेक्ट्रॉन", "प्रोटॉन", "न्यूट्रॉन", "A", "p-प्रकार में त्रिसंयोजी अशुद्धि होती है, जिससे अत्यधिक कोटर बनते हैं।")
    ],
    "Chemistry Class 11": [
        ("कार्बन-12 परमाणुओं के 1 मोल का द्रव्यमान क्या है?", "12 ग्राम", "1.2 ग्राम", "120 ग्राम", "6 ग्राम", "A", "C-12 के 1 मोल का भार ठीक 12 ग्राम होता है।"),
        ("मुख्य क्वांटम संख्या (n) क्या दर्शाता है?", "कक्षक का आकार", "कक्षक की आकृति", "कक्षक का अभिविन्यास", "इलेक्ट्रॉन का चक्रण", "A", "मुख्य क्वांटम संख्या ऊर्जा स्तर और कक्षक के आकार को दर्शाती है।"),
        ("किस तत्व की विद्युत ऋणात्मकता (Electronegativity) सबसे अधिक है?", "फ्लोरीन", "ऑक्सीजन", "क्लोरीन", "नाइट्रोजन", "A", "आवर्त सारणी में फ्लोरीन सबसे अधिक विद्युत ऋणात्मक तत्व है (मान = 4.0)।"),
        ("sp3 संकरण (Hybridization) से कौन सी ज्यामिति जुड़ी है?", "चतुष्फलकीय (Tetrahedral)", "त्रिकोणीय समतलीय", "रेखीय", "अष्टफलकीय", "A", "sp3 संकरण के परिणामस्वरूप 109.5 डिग्री के कोण के साथ चतुष्फलकीय ज्यामिति बनती है।"),
        ("एक आदर्श गैस किस समीकरण का पालन करती है?", "PV = nRT", "PV = RT", "P/V = RT", "PV^2 = nRT", "A", "आदर्श गैस नियम PV = nRT है।"),
        ("एक स्वतः प्रवर्तित प्रक्रम के लिए गिब्स मुक्त ऊर्जा परिवर्तन (dG) होना चाहिए:", "ऋणात्मक", "धनात्मक", "शून्य", "धनात्मक या ऋणात्मक", "A", "स्थिर T और P पर स्वतः प्रवर्तित प्रक्रम के लिए dG < 0 होना अनिवार्य है।"),
        ("HSO4- का संयुग्मी क्षार (Conjugate base) क्या है?", "SO4^2-", "H2SO4", "H+", "OH-", "A", "संयुग्मी क्षार बनाने के लिए एक H+ प्रोटॉन हटाया जाता है, जिससे SO4^2- प्राप्त होता है।"),
        ("धातु हाइड्राइड (जैसे NaH) में हाइड्रोजन की ऑक्सीकरण संख्या होती है:", "-1", "+1", "0", "+2", "A", "धातुएं अधिक विद्युत धनात्मक होती हैं, इसलिए हाइड्रोजन हाइड्राइड के रूप में -1 ऑक्सीकरण अवस्था में होता है।"),
        ("भारी जल (Heavy water) का रासायनिक रूप है:", "ड्यूटेरियम ऑक्साइड (D2O)", "ट्रिटियम ऑक्साइड (T2O)", "हाइड्रोजन पेरोक्साइड (H2O2)", "आसुत जल", "A", "भारी जल में सामान्य हाइड्रोजन के स्थान पर उसका समस्थानिक ड्यूटेरियम होता है।"),
        ("जलीय घोल में कौन सी क्षार धातु सबसे मजबूत अपचायक (Reducing agent) है?", "लिथियम", "सोडियम", "पोटेशियम", "सीजियम", "A", "उच्च जलयोजन ऊर्जा के कारण लिथियम का इलेक्ट्रोड विभव सबसे अधिक ऋणात्मक होता है।")
    ],
    "Chemistry Class 12": [
        ("काय-केंद्रित घनीय (BCC) एकक कोष्ठिका में परमाणुओं की संख्या होती है:", "2", "1", "4", "6", "A", "BCC सेल में 8 कोने * 1/8 + 1 केंद्र * 1 = 2 परमाणु होते हैं।"),
        ("वाष्प दाब का आपेक्षिक अवनमन किसके बराबर होता है?", "विलेय के मोल अंश", "विलेय की मोलरता", "विलायक के मोल अंश", "मोललता", "A", "राउल्ट के नियम के अनुसार, dP/P0 = X_विलेय।"),
        ("नेर्नस्ट समीकरण इलेक्ट्रोड विभव का संबंध किससे दर्शाता है?", "आयनों की सांद्रता", "दाब", "श्यानता", "आयतन", "A", "E = E0 - (RT/nF) * ln(Q), जो विभव को सांद्रता से जोड़ता है।"),
        ("यदि दर स्थिरांक का मात्रक s^-1 है, तो अभिक्रिया की कोटि क्या होगी?", "प्रथम कोटि", "शून्य कोटि", "द्वितीय कोटि", "तृतीय कोटि", "A", "दर = k[A]^1 => k = दर/[A] = (M/s)/M = s^-1।"),
        ("निम्नलिखित में से कौन सा द्रव-स्नेही (Lyophilic) कोलाइड है?", "गोंद", "गोल्ड सॉल", "सल्फर सॉल", "Fe(OH)3 सॉल", "A", "गोंद, जिलेटिन, स्टार्च जैसे कार्बनिक पदार्थ द्रव-स्नेही सॉल बनाते हैं।"),
        ("लोहे के निष्कर्षण में प्रयुक्त गालक (Flux) है:", "चुना पत्थर (Limestone)", "सिलिका", "कोक", "एलुमिना", "A", "चुना पत्थर अपघटित होकर CaO बनाता है जो क्षारीय गालक के रूप में SiO2 अशुद्धि को हटाता है।"),
        ("कौन सी उत्कृष्ट गैस (Noble gas) सबसे अधिक क्रियाशील है?", "जेनॉन (Xenon)", "हीलियम", "नियोन", "ऑर्गन", "A", "जेनॉन की आयनन एन्थैल्पी कम होती है और यह XeF2, XeF4 जैसे यौगिक बनाता है।"),
        ("[Ni(CN)4]^2- में Ni का संकरण क्या है?", "dsp2", "sp3", "d2sp3", "sp3d2", "A", "CN- एक प्रबल क्षेत्र लिगैंड है जो इलेक्ट्रॉनों के युग्मन को मजबूर करता है, जिससे वर्ग समतलीय dsp2 ज्यामिति बनती है।"),
        ("SN1 अभिक्रिया किसमें तेजी से होती है?", "तृतीयक एल्किल हैलाइड", "प्राथमिक एल्किल हैलाइड", "मेथिल हैलाइड", "द्वितीयक एल्किल हैलाइड", "A", "SN1 में कार्बधनायन मध्यवर्ती बनता है, और तृतीयक कार्बधनायन सबसे स्थिर होता है।"),
        ("निम्नलिखित में से कौन सा कार्बोहाइड्रेट एक डाइसैकराइड है?", "सुक्रोज", "ग्लूकोज", "फ्रक्टोज", "स्टार्च", "A", "सुक्रोज जलअपघटित होकर ग्लूकोज और फ्रक्टोज देता है, अतः यह एक डाइसैकराइड है।")
    ],
    "Mathematics Class 11": [
        ("यदि समुच्चय A में n अवयव हैं, तो A के उपसमुच्चयों की संख्या होगी:", "2^n", "n^2", "2n", "n!", "A", "घात समुच्चय (Power set) की कार्डिनैलिटी 2^n होती है।"),
        ("cos(pi) का मान क्या है?", "-1", "1", "0", "1/2", "A", "cos(180 डिग्री) का मान -1 होता है।"),
        ("i^4 का मान क्या है?", "1", "-1", "i", "-i", "A", "i^2 = -1, इसलिए i^4 = (-1)^2 = 1।"),
        ("n विभिन्न वस्तुओं को एक वृत्त में व्यवस्थित करने के तरीकों की संख्या है:", "(n-1)!", "n!", "n^2", "(n+1)!", "A", "वृत्तीय क्रमचय का सूत्र (n-1)! होता है।"),
        ("एक अनंत गुणोत्तर श्रेणी a, ar, ar^2... (|r|<1) के पदों का योग क्या है?", "a / (1 - r)", "a / (1 + r)", "a(1-r)", "a/(r-1)", "A", "अनंत पदों का योग S_inf = a / (1 - r) होता है।"),
        ("बिंदुओं (x1, y1) और (x2, y2) के बीच की दूरी का सूत्र है:", "sqrt((x2-x1)^2 + (y2-y1)^2)", "(x2-x1)^2 + (y2-y1)^2", "x2-x1 + y2-y1", "sqrt(x1^2 + y1^2)", "A", "यह मानक यूक्लिडीय दूरी सूत्र है।"),
        ("y-अक्ष के समांतर रेखा की ढाल (Slope) क्या होती है?", "अपरिभाषित", "0", "1", "-1", "A", "लंबवत रेखा की ढाल tan(90) होती है जो अपरिभाषित है।"),
        ("समीकरण x^2/a^2 - y^2/b^2 = 1 किसको दर्शाता है?", "अतिपरवलय (Hyperbola)", "दीर्घवृत्त (Ellipse)", "परवलय (Parabola)", "वृत्त", "A", "ऋण चिह्न अतिपरवलय को दर्शाता है; धन चिह्न दीर्घवृत्त को दर्शाता है।"),
        ("lim_{x->0} (sin x / x) का मान क्या है?", "1", "0", "अपरिभाषित", "अनंत", "A", "यह एक मानक त्रिकोणमितीय सीमा है जिसका मान 1 होता है।"),
        ("यदि दो घटनाएं परस्पर अपवर्जी (Mutually exclusive) हैं, तो P(A cap B) होगा:", "0", "1", "P(A)*P(B)", "P(A)+P(B)", "A", "परस्पर अपवर्जी का अर्थ है कि वे एक साथ नहीं हो सकतीं; अतः प्रतिच्छेदन शून्य होता है।")
    ],
    "Mathematics Class 12": [
        ("यदि f(x) = x^2, तो f'(x) क्या होगा?", "2x", "x", "2", "3x^2", "A", "x^n का अवकलन n*x^(n-1) होता है।"),
        ("1/x dx का समाकलन क्या है?", "ln|x| + C", "-1/x^2 + C", "e^x + C", "x^2/2 + C", "A", "1/x का समाकलन x के निरपेक्ष मान का प्राकृतिक लघुगणक (ln) प्लस C होता है।"),
        ("अवकल समीकरण (dy/dx)^3 + d^2y/dx^2 = 0 की कोटि (Order) है:", "2", "3", "1", "0", "A", "अवकल समीकरण की कोटि उसमें मौजूद उच्चतम अवकलज (जो कि 2 है) होती है।"),
        ("यदि सदिश A = i + j और B = i - j, तो उनका बिंदु गुणनफल (Dot product) क्या होगा?", "0", "2", "-2", "1", "A", "A.B = (1*1) + (1*-1) = 1 - 1 = 0। ये परस्पर लंबवत हैं।"),
        ("x-अक्ष के समांतर रेखा की दिक्-कोज्याएँ (Direction cosines) हैं:", "1, 0, 0", "0, 1, 0", "0, 0, 1", "1, 1, 1", "A", "x-अक्ष के साथ कोण 0 होता है, cos(0)=1, बाकी अक्षों के साथ 90 कोण होने से cos(90)=0।"),
        ("दो समान पंक्तियों वाले आव्यूह के सारणिक (Determinant) का मान क्या होता है?", "0", "1", "अनंत", "निर्धारित नहीं किया जा सकता", "A", "सारणिकों के गुणधर्म के अनुसार दो समान पंक्तियों होने पर मान 0 होता है।"),
        ("यदि A एक वर्ग आव्यूह है, तो A + A^T हमेशा होता है:", "सममित (Symmetric)", "विषम-सममित", "विकर्ण आव्यूह", "तत्समक आव्यूह", "A", "(A+AT)^T = A^T + (A^T)^T = A^T + A = A + A^T। अतः यह सममित है।"),
        ("e^{3x} का अवकलज क्या है?", "3e^{3x}", "e^{3x}", "3x e^{3x}", "1/3 e^{3x}", "A", "श्रृंखला नियम (Chain Rule) का उपयोग करने पर: d/dx(e^u) = e^u * du/dx।"),
        ("cos(x) dx का समाकलन क्या है?", "sin(x) + C", "-sin(x) + C", "tan(x) + C", "cos(x) + C", "A", "cos(x) का समाकलन sin(x) + C होता है।"),
        ("ताश की गड्डी में से एक लाल पत्ता खींचने की प्रायिकता क्या है?", "1/2", "1/4", "1/13", "2/13", "A", "52 पत्तों में से 26 लाल पत्तों के होने से प्रायिकता 26/52 = 1/2 होती है।")
    ]
}

subject_topics_en = {
    "Physics Class 11": [
        "Physical World & Units of Measurement", "Dimensions & Dimensional Analysis", "Motion in a Straight Line", "Speed, Velocity & Relative Velocity",
        "Motion in a Plane & Vectors", "Projectile Motion", "Newton's First and Second Laws", "Newton's Third Law & Friction",
        "Work, Energy & Power", "Potential Energy & Conservation of Energy", "Center of Mass & Linear Momentum", "Torque & Angular Momentum",
        "Moment of Inertia", "Kepler's Laws of Planetary Motion", "Newton's Law of Gravitation & Acceleration due to gravity", "Elasticity & Hooke's Law",
        "Fluid Mechanics & Bernoulli's Principle", "Thermal Properties of Matter & Heat Transfer", "Thermodynamics (First & Second Laws)", "Simple Harmonic Motion & Waves"
    ],
    "Physics Class 12": [
        "Electric Charges & Coulomb's Law", "Electric Fields & Electric Dipoles", "Gauss's Law & Applications", "Electrostatic Potential",
        "Capacitance & Dielectrics", "Electric Current & Ohm's Law", "Drift Velocity & Resistivity", "Kirchhoff's Rules & Wheatstone Bridge",
        "Biot-Savart Law & Ampere's Law", "Magnetic Force on Charges & Currents", "Magnetic Properties of Matter", "Electromagnetic Induction & Faraday's Law",
        "Lenz's Law & Self/Mutual Induction", "Alternating Current (AC) Circuits & Resonance", "LCR Circuits & Power Factor", "Electromagnetic Waves & EM Spectrum",
        "Reflection, Refraction & Lenses", "Wave Optics (Interference & Diffraction)", "Photoelectric Effect & Dual Nature of Matter", "Bohr's Model of Atom & Semiconductor Electronics"
    ],
    "Chemistry Class 11": [
        "Mole Concept & Empirical Formula", "Stoichiometry & Concentration Terms", "Bohr's Atomic Model & Quantum Numbers", "Electronic Configuration & Pauli's Principle",
        "Periodic Table & Periodic Trends", "Ionic & Covalent Bonding", "VSEPR Theory & Hybridization", "Molecular Orbital Theory & Hydrogen Bonding",
        "Ideal Gas Laws & Dalton's Law", "First Law of Thermodynamics & Enthalpy", "Entropy & Gibbs Free Energy", "Law of Mass Action & Equilibrium Constant",
        "Le Chatelier's Principle", "pH Scale & Buffer Solutions", "Solubility Product & Salt Hydrolysis", "Oxidation & Reduction (Redox Reactions)",
        "Balancing Redox Reactions", "Nomenclature of Organic Compounds (IUPAC)", "Isomerism in Organic Chemistry", "Inductive Effect & Resonance"
    ],
    "Chemistry Class 12": [
        "Solid State", "Solutions & Vapor pressure", "Colligative Properties", "Electrochemistry & Nernst equation",
        "Faraday's Laws & Electrolytic conductance", "Chemical Kinetics & Order of reaction", "Arrhenius Equation & Activation Energy", "Surface Chemistry",
        "Principles of Metallurgy", "p-Block Elements", "Transition Elements (d & f block)", "Coordination Compounds",
        "Valence Bond & Crystal Field Theory", "Haloalkanes & Haloarenes", "Alcohols, Phenols & Ethers", "Aldehydes & Ketones",
        "Carboxylic Acids & Amines", "Biomolecules & Glucose structure", "Proteins, Amino Acids & Nucleic Acids", "Polymers & Chemistry in Everyday Life"
    ],
    "Mathematics Class 11": [
        "Sets, Subsets & Power Sets", "Relations & Functions", "Trigonometric Functions & Identities", "Principle of Mathematical Induction",
        "Complex Numbers & Quadratic Equations", "Linear Inequalities", "Permutations", "Combinations",
        "Binomial Theorem & General Term", "Arithmetic Progression (AP)", "Geometric Progression (GP)", "Straight Lines & Slope of a line",
        "Conic Sections (Circles & Parabola)", "Conic Sections (Ellipse & Hyperbola)", "3D Coordinate Geometry", "Limits of functions",
        "Derivatives of functions", "Mathematical Reasoning", "Statistics (Variance)", "Probability"
    ],
    "Mathematics Class 12": [
        "Relations & Functions", "Inverse Trigonometric Functions", "Matrices & Transpose", "Symmetric & Skew-symmetric matrices",
        "Determinants & Properties", "Adjoint & Inverse of a Matrix", "Continuity of functions", "Differentiability & Chain Rule",
        "Logarithmic & Parametric Differentiation", "Tangents & Normals", "Maxima & Minima", "Indefinite Integrals",
        "Integrals by Parts & Partial Fractions", "Definite Integrals & Properties", "Area under Simple Curves", "Ordinary Differential Equations",
        "Homogeneous & Linear Differential Equations", "Vector Algebra (Dot & Cross products)", "Three Dimensional Geometry", "Linear Programming & Bayes' Theorem"
    ]
}

subject_topics_hi = {
    "Physics Class 11": [
        "भौतिक जगत और मापन", "विमाएं और विमीय विश्लेषण", "सरल रेखा में गति", "चाल, वेग और सापेक्ष वेग",
        "समतल में गति और सदिश", "प्रक्षेप्य गति", "न्यूटन के प्रथम और द्वितीय नियम", "न्यूटन का तृतीय नियम और घर्षण",
        "कार्य, ऊर्जा और शक्ति", "स्थितिज ऊर्जा और ऊर्जा संरक्षण", "द्रव्यमान केंद्र और रेखीय संवेग", "बल आघूर्ण और कोणीय संवेग",
        "जड़त्व आघूर्ण", "केप्लर के नियम", "गुरुत्वाकर्षण का नियम और गुरुत्वीय त्वरण", "प्रत्यास्थता और हुक का नियम",
        "तरल यांत्रिकी और बरनौली का सिद्धांत", "द्रव्य के तापीय गुण और ऊष्मा स्थानांतरण", "ऊष्मागतिकी (प्रथम और द्वितीय नियम)", "सरल आवर्त गति और तरंगें"
    ],
    "Physics Class 12": [
        "विद्युत आवेश और कूलॉम का नियम", "विद्युत क्षेत्र और विद्युत द्विध्रुव", "गॉस का नियम और अनुप्रयोग", "स्थिरविद्युत विभव",
        "धारिता और परावैद्युत", "विद्युत धारा और ओम का नियम", "अपवाह वेग और प्रतिरोधकता", "किरचॉफ के नियम और व्हीटस्टोन ब्रिज",
        "बायो-सावर नियम और एम्पीयर का नियम", "आवेशों और धाराओं पर चुंबकीय बल", "द्रव्य के चुंबकीय गुण", "विद्युत चुंबकीय प्रेरण और फैराडे का नियम",
        "लेंज का नियम और स्व/अन्योन्य प्रेरण", "प्रत्यावर्ती धारा (AC) परिपथ और अनुनाद", "LCR परिपथ और शक्ति गुणांक", "विद्युत चुंबकीय तरंगें और स्पेक्ट्रम",
        "परावर्तन, अपवर्तन और लेंस", "तरंग प्रकाशिकी (व्यतिकरण और विवर्तन)", "प्रकाश विद्युत प्रभाव और द्रव्य की द्वैत प्रकृति", "परमाणु का बोहर मॉडल और अर्धचालक इलेक्ट्रॉनिक्स"
    ],
    "Chemistry Class 11": [
        "मोल अवधारणा और मूलानुपाती सूत्र", "स्टोइकियोमेट्री और सांद्रता पद", "बोहर का परमाणु मॉडल और क्वांटम संख्याएं", "इलेक्ट्रॉनिक विन्यास और पाउली का नियम",
        "आवर्त सारणी और आवर्ती रुझान", "आयनिक और सहसंयोजक आबंधन", "VSEPR सिद्धांत और संकरण", "आणविक कक्षक सिद्धांत और हाइड्रोजन आबंधन",
        "आदर्श गैस नियम और डाल्टन का नियम", "ऊष्मागतिकी का प्रथम नियम और एन्थैल्पी", "एन्ट्रॉपी और गिब्ज मुक्त ऊर्जा", "द्रव्य अनुपाती क्रिया का नियम और साम्यावस्था स्थिरांक",
        "ली-शातेलिए का सिद्धांत", "pH पैमाना और बफर विलयन", "विलेयता गुणनफल और लवण जलअपघटन", "ऑक्सीकरण और अपचयन (रेडॉक्स अभिक्रियाएं)",
        "रेडॉक्स अभिक्रियाओं को संतुलित करना", "कार्बनिक यौगिकों का नामकरण (IUPAC)", "कार्बनिक रसायन में समावयवता", "प्रेरणिक प्रभाव और अनुनाद"
    ],
    "Chemistry Class 12": [
        "ठोस अवस्था", "विलयन और वाष्प दाब", "अणुसंख्यक गुणधर्म", "विद्युत रसायन और नेर्नस्ट समीकरण",
        "फैराडे के नियम और विद्युत चालकता", "रासायनिक बलगतिकी और अभिक्रिया की कोटि", "आरहेनियस समीकरण और सक्रियण ऊर्जा", "सतह रसायन (अधिशोषण और कोलाइड)",
        "धातु कर्म के सिद्धांत", "p-ब्लॉक के तत्व", "संक्रमण तत्व (d और f ब्लॉक)", "उपसहसंयोजक यौगिक",
        "संयोजकता आबंध और क्रिस्टल क्षेत्र सिद्धांत", "हैलोएल्केन और हैलोएरीन", "अल्कोहल, फिनोल और ईथर", "एल्डिहाइड और कीटोन",
        "कार्बोक्सिलिक अम्ल और एमीन", "जैव अणु और ग्लूकोज संरचना", "प्रोटीन, अमीनो अम्ल और न्यूक्लिक अम्ल", "बहुलक और दैनिक जीवन में रसायन"
    ],
    "Mathematics Class 11": [
        "समुच्चय, उपसमुच्चय और घात समुच्चय", "संबंध और फलन", "त्रिकोणमितीय फलन और सर्वसमिकाएं", "गणितीय आगमन का सिद्धांत",
        "सम्मिश्र संख्याएं और द्विघात समीकरण", "रैखिक असमिकाएं", "क्रमचय", "संचय",
        "द्विपद प्रमेय और व्यापक पद", "समांतर श्रेणी (AP)", "गुणोत्तर श्रेणी (GP)", "सरल रेखाएं और रेखा की ढाल",
        "शंकु परिच्छेद (वृत्त और परवलय)", "शंकु परिच्छेद (दीर्घवृत्त और अतिपरवलय)", "त्रिविमीय निर्देशांक ज्यामिति", "फलनों की सीमाएं",
        "फलनों के अवकलज", "गणितीय विवेचन", "सांख्यिकी (प्रसरण)", "प्रायिकता"
    ],
    "Mathematics Class 12": [
        "संबंध और फलन", "प्रतिलोम त्रिकोणमितीय फलन", "आव्यूह और परिवर्त", "सममित और विषम-सममित आव्यूह",
        "सारणिक और गुणधर्म", "आव्यूह का सहखंडज और व्युत्क्रम", "फलनों की सततता", "अवकलनीयता और श्रृंखला नियम",
        "लघुगणकीय और प्राचलिक अवकलन", "स्पर्श रेखाएं और अभिलंब", "उच्चिष्ठ और निम्निष्ठ", "अनिश्चित समाकलन",
        "खंडशः समाकलन और आंशिक भिन्न", "निश्चित समाकलन और गुणधर्म", "साधारण वक्रों के अंतर्गत क्षेत्रफल", "साधारण अवकल समीकरण",
        "समघातीय और रैखिक अवकल समीकरण", "सदिश बीजगणित (डॉट और क्रॉस गुणनफल)", "त्रिविमीय ज्यामिति", "रैखिक प्रोग्रामन और बेज प्रमेय"
    ]
}

now = datetime.datetime.now(datetime.timezone.utc)
now_str = now.strftime('%Y-%m-%d %H:%M:%S+00')

# Combined templates
all_templates = templates_en + templates_hi

# 1. Generate Templates CSV
templates_path = "d:/VidyaSetu AI/vidyastu_mboile_app/global_journey_templates.csv"
with open(templates_path, mode='w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    # Header
    writer.writerow([
        "id", "title", "description", "duration_days", "category", "version",
        "is_active", "is_deleted", "created_at", "updated_at", "created_by", "updated_by"
    ])
    for t in all_templates:
        writer.writerow([
            t["id"], t["title"], t["description"], t["days"], t["category"], 1,
            True, False, now_str, now_str, "", ""
        ])

# 2. Generate Tasks CSV
tasks_path = "d:/VidyaSetu AI/vidyastu_mboile_app/global_journey_tasks.csv"
with open(tasks_path, mode='w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    # Header
    writer.writerow([
        "id", "template_id", "day_number", "task_title", "task_description", "task_type",
        "sort_order", "is_active", "is_deleted", "created_at", "updated_at", "created_by", "updated_by"
    ])
    
    # Generate for English templates
    for t in templates_en:
        cat = t["category"]
        topics_list = subject_topics_en.get(cat, ["General Study"] * 20)
        for day in range(1, t["days"] + 1):
            topic_name = topics_list[(day - 1) % len(topics_list)]
            task_id = str(uuid.uuid4())
            title = f"Day {day} Topic: {topic_name}"
            description = f"Today's MCQ test will be based on the topic '{topic_name}'. Please study this topic in detail from your NCERT book and classroom notes."
            writer.writerow([
                task_id, t["id"], day, title, description, "read",
                1, True, False, now_str, now_str, "", ""
            ])
            
    # Generate for Hindi templates
    for t in templates_hi:
        cat = t["category"]
        topics_list = subject_topics_hi.get(cat, ["सामान्य अध्ययन"] * 20)
        for day in range(1, t["days"] + 1):
            topic_name = topics_list[(day - 1) % len(topics_list)]
            task_id = str(uuid.uuid4())
            title = f"दिन {day} टॉपिक: {topic_name}"
            description = f"आज का MCQ टेस्ट इसी टॉपिक '{topic_name}' पर आधारित रहेगा। कृपया एनसीईआरटी पुस्तक और क्लास नोट्स से इसका अच्छी तरह अध्ययन करें।"
            writer.writerow([
                task_id, t["id"], day, title, description, "read",
                1, True, False, now_str, now_str, "", ""
            ])

# 3. Generate MCQs CSV
mcqs_path = "d:/VidyaSetu AI/vidyastu_mboile_app/global_journey_mcqs.csv"
with open(mcqs_path, mode='w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    # Header
    writer.writerow([
        "id", "template_id", "day_number", "question_text", "option_a", "option_b",
        "option_c", "option_d", "correct_option", "explanation", "difficulty_level",
        "points", "is_active", "is_deleted", "created_at", "updated_at", "created_by", "updated_by"
    ])
    
    # Generate for English templates
    for t in templates_en:
        cat = t["category"]
        questions_list = pcm_questions_en.get(cat, pcm_questions_en["Physics Class 11"])
        for day in range(1, t["days"] + 1):
            for q_index in range(1, 21):
                sample_q = questions_list[(q_index - 1) % len(questions_list)]
                mcq_id = str(uuid.uuid4())
                q_text = f"Day {day} - Q{q_index}: {sample_q[0]}"
                writer.writerow([
                    mcq_id, t["id"], day, q_text, sample_q[1], sample_q[2],
                    sample_q[3], sample_q[4], sample_q[5], sample_q[6], "medium",
                    1, True, False, now_str, now_str, "", ""
                ])
                
    # Generate for Hindi templates
    for t in templates_hi:
        cat = t["category"]
        questions_list = pcm_questions_hi.get(cat, pcm_questions_hi["Physics Class 11"])
        for day in range(1, t["days"] + 1):
            for q_index in range(1, 21):
                sample_q = questions_list[(q_index - 1) % len(questions_list)]
                mcq_id = str(uuid.uuid4())
                q_text = f"दिन {day} - प्रश्न {q_index}: {sample_q[0]}"
                writer.writerow([
                    mcq_id, t["id"], day, q_text, sample_q[1], sample_q[2],
                    sample_q[3], sample_q[4], sample_q[5], sample_q[6], "medium",
                    1, True, False, now_str, now_str, "", ""
                ])

print("CSV files generated successfully:")
print(f"1. {templates_path}")
print(f"2. {tasks_path}")
print(f"3. {mcqs_path}")
