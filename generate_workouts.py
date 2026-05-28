#!/usr/bin/env python3
"""Generate seed_workouts.json with 65 bodyweight exercises and 90 routines."""

import json, os

# ─────────────────────────────────────────────────────────────
# EXERCISES  (65 total)
# ─────────────────────────────────────────────────────────────
exercises = [
    # ── Push-ups (1-8) ──────────────────────────────────────
    {
        "id": "ex_001", "name": "Standard Push-Up",
        "description": "Classic upper body exercise targeting chest, shoulders, and triceps. Builds foundational pressing strength and endurance.",
        "muscleGroups": ["chest", "shoulders", "triceps"], "difficulty": "beginner",
        "instructions": [
            "Start in a high plank position with hands placed slightly wider than shoulder-width apart, fingers pointing forward.",
            "Keep your body in a perfectly straight line from head to heels by engaging your core and squeezing your glutes.",
            "Lower your chest toward the floor by bending your elbows to approximately 90 degrees, keeping elbows at a 45-degree angle from your body.",
            "Push through your palms to fully extend your arms and return to the starting position.",
            "Exhale as you push up and inhale as you lower down. Maintain a controlled tempo throughout."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_002", "name": "Wide Push-Up",
        "description": "A push-up variation with hands placed wider than shoulder-width to emphasize the outer chest and anterior deltoids.",
        "muscleGroups": ["chest", "shoulders"], "difficulty": "beginner",
        "instructions": [
            "Begin in a high plank position with your hands placed about 1.5 times shoulder-width apart.",
            "Keep your core tight and body in a straight line from head to heels throughout the movement.",
            "Lower your chest toward the floor, allowing your elbows to flare out slightly wider than a standard push-up.",
            "Press through your palms to return to the starting position, fully extending your arms at the top.",
            "Move slowly and with control; avoid letting your hips sag or pike upward."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_003", "name": "Diamond Push-Up",
        "description": "An advanced push-up variation with hands close together forming a diamond shape, heavily targeting the triceps and inner chest.",
        "muscleGroups": ["triceps", "chest", "shoulders"], "difficulty": "intermediate",
        "instructions": [
            "Start in a high plank position and bring your hands together directly under your chest, touching your thumbs and index fingers to form a diamond shape.",
            "Keep your elbows close to your body as you lower your chest toward your hands.",
            "Lower until your chest nearly touches your hands, maintaining a tight core and straight body line.",
            "Push through your palms to extend your arms and return to the starting position.",
            "If this is too difficult, perform the movement on your knees while maintaining proper hand position."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 45, "isTimeBased": False
    },
    {
        "id": "ex_004", "name": "Incline Push-Up",
        "description": "A beginner-friendly push-up performed with hands elevated on a sturdy surface, reducing the load and making the movement more accessible.",
        "muscleGroups": ["chest", "shoulders", "triceps"], "difficulty": "beginner",
        "instructions": [
            "Place your hands on a sturdy elevated surface such as a bench, step, or counter at about waist height, shoulder-width apart.",
            "Walk your feet back until your body forms a straight line from head to heels at an inclined angle.",
            "Lower your chest toward the surface by bending your elbows, keeping them at a 45-degree angle from your torso.",
            "Push through your palms to return to the starting position with full arm extension.",
            "As you get stronger, use progressively lower surfaces to increase the challenge."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_005", "name": "Decline Push-Up",
        "description": "An advanced push-up with feet elevated on a surface, shifting more load to the upper chest and shoulders.",
        "muscleGroups": ["chest", "shoulders", "triceps"], "difficulty": "intermediate",
        "instructions": [
            "Place your feet on a stable elevated surface such as a chair, bench, or step behind you.",
            "Position your hands on the floor slightly wider than shoulder-width apart, arms fully extended.",
            "Keep your core braced and body in a straight line from your elevated heels to your head.",
            "Lower your chest toward the floor by bending your elbows to 90 degrees.",
            "Press through your palms to push back up to the starting position. Avoid flaring your elbows excessively."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 45, "isTimeBased": False
    },
    {
        "id": "ex_006", "name": "Pike Push-Up",
        "description": "A shoulder-dominant push-up variation performed in an inverted V position, building overhead pressing strength and deltoid development.",
        "muscleGroups": ["shoulders", "triceps"], "difficulty": "intermediate",
        "instructions": [
            "Start in a downward dog position with your hips piked high, hands shoulder-width apart, and feet hip-width apart.",
            "Your body should form an inverted V shape with your head between your arms, looking back toward your feet.",
            "Bend your elbows and lower the top of your head toward the floor between your hands.",
            "Press through your palms to straighten your arms and return to the starting pike position.",
            "Keep your legs as straight as possible and focus on pressing vertically rather than horizontally."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 45, "isTimeBased": False
    },
    {
        "id": "ex_007", "name": "Tricep Push-Up",
        "description": "A push-up variation with hands placed close together and elbows kept tight to the body, maximally engaging the triceps.",
        "muscleGroups": ["triceps", "chest"], "difficulty": "intermediate",
        "instructions": [
            "Start in a high plank position with your hands placed directly under your shoulders, shoulder-width apart or slightly narrower.",
            "As you lower your body, keep your elbows tucked tightly against your ribs rather than flaring them out.",
            "Lower until your chest is just above the floor, feeling a strong stretch in your triceps.",
            "Push through your palms and extend your arms fully, squeezing your triceps at the top.",
            "Maintain a straight body line throughout and avoid letting your hips drop or rise."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 45, "isTimeBased": False
    },
    {
        "id": "ex_008", "name": "Spiderman Push-Up",
        "description": "A dynamic push-up that incorporates a knee drive to the elbow, engaging the core and obliques while building upper body strength.",
        "muscleGroups": ["chest", "core", "shoulders"], "difficulty": "advanced",
        "instructions": [
            "Begin in a standard high plank push-up position with hands slightly wider than shoulder-width.",
            "As you lower your chest toward the floor, simultaneously drive your right knee toward your right elbow.",
            "Your knee should reach your elbow at the bottom of the push-up movement.",
            "Push back up to the starting position while returning your right foot to the plank position.",
            "Alternate sides with each rep, keeping your core engaged and hips level throughout the movement."
        ],
        "defaultReps": 6, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 60, "isTimeBased": False
    },
    # ── Squats (9-15) ──────────────────────────────────────
    {
        "id": "ex_009", "name": "Standard Squat",
        "description": "The fundamental lower body exercise that targets quads, glutes, and hamstrings. Essential for building leg strength and toning.",
        "muscleGroups": ["quads", "glutes", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet shoulder-width apart, toes pointing slightly outward at about 15-30 degrees.",
            "Initiate the movement by pushing your hips back and bending your knees as if sitting into a chair.",
            "Lower until your thighs are parallel to the floor or as deep as your mobility allows, keeping your chest lifted.",
            "Drive through your heels to stand back up, squeezing your glutes at the top.",
            "Keep your knees tracking over your toes and avoid letting them cave inward throughout the movement."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_010", "name": "Sumo Squat",
        "description": "A wide-stance squat variation that targets the inner thighs, glutes, and quads with an emphasis on hip mobility.",
        "muscleGroups": ["quads", "glutes", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet wider than shoulder-width apart, toes pointed outward at about 45 degrees.",
            "Clasp your hands in front of your chest or extend your arms forward for balance.",
            "Lower your body by bending your knees and pushing your hips back, keeping your torso upright.",
            "Descend until your thighs are parallel to the floor, feeling a stretch in your inner thighs.",
            "Press through your heels to return to standing, squeezing your glutes and inner thighs at the top."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_011", "name": "Jump Squat",
        "description": "An explosive plyometric squat that builds lower body power, burns calories, and elevates heart rate for cardiovascular benefit.",
        "muscleGroups": ["quads", "glutes", "cardio"], "difficulty": "intermediate",
        "instructions": [
            "Stand with feet shoulder-width apart and perform a standard squat, lowering until thighs are parallel to the floor.",
            "From the bottom of the squat, explosively drive through your heels and jump as high as you can.",
            "Swing your arms upward to generate momentum and achieve maximum height.",
            "Land softly on the balls of your feet, immediately bending your knees to absorb the impact.",
            "Transition smoothly into the next squat without pausing. Keep landings quiet and controlled."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 45, "isTimeBased": False
    },
    {
        "id": "ex_012", "name": "Pistol Squat Progression",
        "description": "A challenging single-leg squat progression that develops unilateral leg strength, balance, and mobility. Begin with assisted versions.",
        "muscleGroups": ["quads", "glutes", "core"], "difficulty": "advanced",
        "instructions": [
            "Stand on one leg with the other leg extended straight in front of you, arms reaching forward for balance.",
            "Slowly bend your standing knee and lower your body as deep as you can while keeping the extended leg off the floor.",
            "Keep your standing heel firmly planted and your chest as upright as possible.",
            "Push through your standing heel to return to the upright position.",
            "If you cannot perform a full pistol squat, hold onto a doorframe or chair for assistance, or lower onto a chair behind you."
        ],
        "defaultReps": 5, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 60, "isTimeBased": False
    },
    {
        "id": "ex_013", "name": "Wall Sit",
        "description": "An isometric hold against a wall that builds quad endurance and mental toughness while being gentle on the joints.",
        "muscleGroups": ["quads", "glutes"], "difficulty": "beginner",
        "instructions": [
            "Stand with your back flat against a wall, feet about two feet away from the wall and shoulder-width apart.",
            "Slide your back down the wall until your thighs are parallel to the floor and your knees are bent at 90 degrees.",
            "Keep your back fully pressed against the wall and your weight in your heels.",
            "Hold this seated position for the prescribed duration, breathing steadily throughout.",
            "To release, slide back up the wall or step forward. Avoid pushing off your knees to stand."
        ],
        "defaultReps": None, "defaultSets": 3, "defaultHoldSeconds": 30, "restSeconds": 30, "isTimeBased": True
    },
    {
        "id": "ex_014", "name": "Squat Pulse",
        "description": "A squat variation that keeps muscles under constant tension through small pulsing movements at the bottom of the squat position.",
        "muscleGroups": ["quads", "glutes"], "difficulty": "intermediate",
        "instructions": [
            "Stand with feet shoulder-width apart and lower into a squat until your thighs are parallel to the floor.",
            "Instead of standing all the way up, pulse up and down about 3-4 inches from the bottom position.",
            "Keep your weight in your heels and your chest lifted throughout the pulsing movement.",
            "Maintain continuous tension in your quads and glutes by never fully straightening your legs.",
            "Complete the prescribed number of pulses, then stand fully to finish the set."
        ],
        "defaultReps": 15, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_015", "name": "Squat Hold",
        "description": "An isometric squat hold at the bottom position that builds quad endurance, hip mobility, and postural strength.",
        "muscleGroups": ["quads", "glutes"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet shoulder-width apart, toes slightly turned out.",
            "Lower into a deep squat position, keeping your chest lifted and weight in your heels.",
            "Hold the bottom position with thighs parallel to the floor or slightly below.",
            "Extend your arms forward for balance and breathe steadily throughout the hold.",
            "Maintain an upright torso and avoid rounding your lower back during the hold."
        ],
        "defaultReps": None, "defaultSets": 3, "defaultHoldSeconds": 20, "restSeconds": 30, "isTimeBased": True
    },
    # ── Lunges (16-19) ─────────────────────────────────────
    {
        "id": "ex_016", "name": "Forward Lunge",
        "description": "A fundamental unilateral lower body exercise that targets the quads, glutes, and hamstrings while improving balance.",
        "muscleGroups": ["quads", "glutes", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Stand tall with feet hip-width apart and hands on your hips or at your sides.",
            "Take a large step forward with your right foot, landing heel first.",
            "Lower your body until both knees are bent at approximately 90 degrees, with your back knee hovering just above the floor.",
            "Push through your front heel to drive yourself back to the starting standing position.",
            "Alternate legs with each rep. Keep your torso upright and core engaged throughout."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_017", "name": "Reverse Lunge",
        "description": "A knee-friendly lunge variation where you step backward, placing more emphasis on the glutes and reducing stress on the front knee.",
        "muscleGroups": ["quads", "glutes", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Stand tall with feet hip-width apart and hands on your hips.",
            "Step backward with your right foot, landing on the ball of your foot.",
            "Lower your body until your front thigh is parallel to the floor and your back knee nearly touches the ground.",
            "Push through the heel of your front foot to return to the standing position.",
            "Alternate legs each rep. Keep your front knee tracked over your ankle and your torso upright."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_018", "name": "Side Lunge",
        "description": "A lateral lunge that targets the inner and outer thighs, glutes, and quads while improving lateral mobility.",
        "muscleGroups": ["quads", "glutes", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet together and hands clasped in front of your chest.",
            "Take a large step to the right, bending your right knee and pushing your hips back while keeping your left leg straight.",
            "Lower until your right thigh is parallel to the floor, keeping your chest up and weight in your right heel.",
            "Push through your right foot to return to the starting position.",
            "Complete all reps on one side before switching, or alternate sides each rep."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_019", "name": "Curtsy Lunge",
        "description": "A crossover lunge variation that uniquely targets the gluteus medius and outer hip, improving hip stability and lateral balance.",
        "muscleGroups": ["glutes", "quads"], "difficulty": "intermediate",
        "instructions": [
            "Stand with feet hip-width apart and hands on your hips or clasped at chest level.",
            "Step your right foot diagonally behind your left leg, as if performing a curtsy.",
            "Bend both knees and lower until your front thigh is parallel to the floor.",
            "Keep your torso upright and your front knee tracked over your front toes throughout the movement.",
            "Push through your front heel to return to standing and alternate sides."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    # ── Planks (20-25) ─────────────────────────────────────
    {
        "id": "ex_020", "name": "Forearm Plank",
        "description": "A foundational isometric core exercise that strengthens the entire midsection, shoulders, and back while improving posture.",
        "muscleGroups": ["core"], "difficulty": "beginner",
        "instructions": [
            "Start face-down on the floor, then prop yourself up on your forearms with elbows directly under your shoulders.",
            "Extend your legs behind you, resting on the balls of your feet, hip-width apart.",
            "Engage your core, squeeze your glutes, and lift your body into a straight line from head to heels.",
            "Hold this position without letting your hips sag toward the floor or pike upward.",
            "Breathe steadily and focus on maintaining tension in your abs throughout the entire hold."
        ],
        "defaultReps": None, "defaultSets": 3, "defaultHoldSeconds": 30, "restSeconds": 30, "isTimeBased": True
    },
    {
        "id": "ex_021", "name": "Side Plank",
        "description": "A lateral core exercise that targets the obliques, hip stabilizers, and shoulders while building anti-lateral-flexion strength.",
        "muscleGroups": ["core", "shoulders"], "difficulty": "intermediate",
        "instructions": [
            "Lie on your right side with your right forearm on the floor, elbow directly under your shoulder.",
            "Stack your feet on top of each other or stagger them for more stability.",
            "Lift your hips off the floor until your body forms a straight line from head to feet.",
            "Hold this position, keeping your core tight and hips lifted. Avoid letting your hips dip.",
            "Complete the hold on one side, then switch to the other side. Breathe steadily throughout."
        ],
        "defaultReps": None, "defaultSets": 3, "defaultHoldSeconds": 20, "restSeconds": 30, "isTimeBased": True
    },
    {
        "id": "ex_022", "name": "High Plank",
        "description": "A plank variation performed on straight arms that engages the chest, shoulders, and core while building wrist strength.",
        "muscleGroups": ["core", "shoulders", "chest"], "difficulty": "beginner",
        "instructions": [
            "Start in the top of a push-up position with hands directly under your shoulders, arms fully extended.",
            "Keep your body in a straight line from the crown of your head to your heels.",
            "Engage your core by pulling your belly button toward your spine and squeeze your glutes.",
            "Spread your fingers wide and press firmly through your palms to protect your wrists.",
            "Hold the position for the prescribed duration, breathing rhythmically and avoiding any sagging or piking."
        ],
        "defaultReps": None, "defaultSets": 3, "defaultHoldSeconds": 30, "restSeconds": 30, "isTimeBased": True
    },
    {
        "id": "ex_023", "name": "Plank Walk",
        "description": "A dynamic plank variation that transitions between forearm and high plank positions, building shoulder endurance and core stability.",
        "muscleGroups": ["core", "shoulders", "chest"], "difficulty": "intermediate",
        "instructions": [
            "Begin in a forearm plank position with elbows under shoulders and body in a straight line.",
            "Place your right hand flat on the floor where your right elbow was, then your left hand where your left elbow was, pushing up to a high plank.",
            "Reverse the movement by lowering your right forearm back to the floor, then your left forearm.",
            "Keep your hips as stable as possible throughout the transitions, minimizing side-to-side rocking.",
            "Alternate the leading arm each rep to develop both sides evenly."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 45, "isTimeBased": False
    },
    {
        "id": "ex_024", "name": "Plank Shoulder Taps",
        "description": "An anti-rotation core exercise performed from a high plank, challenging stability while targeting shoulders and deep core muscles.",
        "muscleGroups": ["core", "shoulders"], "difficulty": "intermediate",
        "instructions": [
            "Start in a high plank position with hands slightly wider than shoulder-width to increase your base of support.",
            "While maintaining a rigid, straight body line, lift your right hand and tap your left shoulder.",
            "Return your right hand to the floor and repeat with your left hand tapping your right shoulder.",
            "Focus on keeping your hips completely still and squared to the floor — avoid rocking side to side.",
            "Move slowly and deliberately; the goal is stability, not speed."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_025", "name": "Reverse Plank",
        "description": "A posterior chain plank that targets the glutes, lower back, and rear shoulders while opening the chest and hip flexors.",
        "muscleGroups": ["core", "back", "glutes"], "difficulty": "intermediate",
        "instructions": [
            "Sit on the floor with legs extended in front of you and hands placed behind your hips, fingers pointing toward your feet.",
            "Press through your palms and heels to lift your hips off the ground until your body forms a straight line from shoulders to ankles.",
            "Squeeze your glutes and engage your core to keep your hips elevated.",
            "Keep your chin slightly tucked and gaze toward the ceiling.",
            "Hold for the prescribed duration, then slowly lower your hips back to the floor."
        ],
        "defaultReps": None, "defaultSets": 3, "defaultHoldSeconds": 20, "restSeconds": 30, "isTimeBased": True
    },
    # ── Core (26-36) ───────────────────────────────────────
    {
        "id": "ex_026", "name": "Crunches",
        "description": "A classic abdominal exercise that isolates the upper rectus abdominis through a short-range spinal flexion movement.",
        "muscleGroups": ["core"], "difficulty": "beginner",
        "instructions": [
            "Lie on your back with knees bent, feet flat on the floor hip-width apart.",
            "Place your fingertips lightly behind your ears or cross your arms over your chest.",
            "Engage your core and curl your shoulders off the floor by contracting your abs, lifting about 30 degrees.",
            "Hold the top position for a brief moment, squeezing your abs, then slowly lower back down.",
            "Avoid pulling on your neck with your hands. Focus on curling your ribcage toward your pelvis."
        ],
        "defaultReps": 15, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_027", "name": "Bicycle Crunches",
        "description": "A dynamic crunch variation that targets the obliques and rectus abdominis through a pedaling motion with alternating elbow-to-knee contact.",
        "muscleGroups": ["core"], "difficulty": "beginner",
        "instructions": [
            "Lie on your back and lift your shoulders off the floor. Place your fingertips behind your ears.",
            "Lift both feet off the ground and bend your knees at 90 degrees in a tabletop position.",
            "Simultaneously extend your right leg straight while rotating your torso to bring your right elbow toward your left knee.",
            "Switch sides in a smooth pedaling motion, bringing your left elbow to your right knee as you extend your left leg.",
            "Move in a controlled, continuous rhythm. Avoid pulling on your neck; the rotation should come from your torso."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_028", "name": "Leg Raises",
        "description": "A lower abdominal exercise that strengthens the hip flexors and lower rectus abdominis by lifting the legs while keeping the back flat.",
        "muscleGroups": ["core"], "difficulty": "intermediate",
        "instructions": [
            "Lie flat on your back with legs extended and arms at your sides, palms pressing into the floor for stability.",
            "Press your lower back firmly into the floor to protect your spine throughout the movement.",
            "Keeping your legs straight, slowly raise them together until they point straight up toward the ceiling.",
            "Lower your legs back down in a slow, controlled manner, stopping just before your heels touch the floor.",
            "If keeping legs straight is too difficult, perform the movement with a slight bend in your knees."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_029", "name": "Mountain Climbers",
        "description": "A dynamic full-body exercise that combines core engagement with cardiovascular conditioning through rapid alternating knee drives.",
        "muscleGroups": ["core", "cardio"], "difficulty": "beginner",
        "instructions": [
            "Start in a high plank position with hands directly under shoulders and body in a straight line.",
            "Drive your right knee toward your chest while keeping your left leg extended.",
            "Quickly switch legs, extending your right leg back while driving your left knee toward your chest.",
            "Continue alternating legs in a running motion while maintaining a stable plank position.",
            "Keep your hips level and core engaged. Start slowly for form, then increase speed as you improve."
        ],
        "defaultReps": 20, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_030", "name": "Dead Bugs",
        "description": "A core stability exercise that trains anti-extension strength and coordination by moving opposite arm and leg while maintaining a flat back.",
        "muscleGroups": ["core"], "difficulty": "beginner",
        "instructions": [
            "Lie on your back with arms extended straight toward the ceiling and knees bent at 90 degrees in a tabletop position.",
            "Press your lower back firmly into the floor and brace your core — maintain this contact throughout.",
            "Slowly lower your right arm overhead and extend your left leg toward the floor simultaneously.",
            "Return to the starting position and repeat with the left arm and right leg.",
            "Move slowly and deliberately. If your lower back lifts off the floor, reduce your range of motion."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_031", "name": "Bird Dogs",
        "description": "A core and back stabilization exercise that improves balance and coordination by extending opposite arm and leg from a hands-and-knees position.",
        "muscleGroups": ["core", "back"], "difficulty": "beginner",
        "instructions": [
            "Start on your hands and knees with wrists under shoulders and knees under hips in a tabletop position.",
            "Engage your core and simultaneously extend your right arm forward and left leg backward until both are parallel to the floor.",
            "Hold the extended position for 1-2 seconds, focusing on keeping your hips level and squared to the floor.",
            "Slowly return your hand and knee to the starting position with control.",
            "Alternate sides each rep. Avoid arching your lower back or rotating your hips during the movement."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_032", "name": "Russian Twists",
        "description": "A seated rotational core exercise that targets the obliques and transverse abdominis through controlled side-to-side twisting.",
        "muscleGroups": ["core"], "difficulty": "intermediate",
        "instructions": [
            "Sit on the floor with knees bent and feet flat. Lean back slightly to about 45 degrees, keeping your back straight.",
            "Clasp your hands together in front of your chest or hold them at chest level.",
            "Lift your feet off the floor slightly for an added challenge, or keep them grounded for stability.",
            "Rotate your torso to the right, bringing your hands beside your right hip, then rotate to the left.",
            "Move in a controlled rhythm, exhaling with each rotation. Keep your core tight and avoid rounding your back."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_033", "name": "Flutter Kicks",
        "description": "A lower abdominal exercise that builds endurance through continuous small alternating leg kicks while lying supine.",
        "muscleGroups": ["core"], "difficulty": "intermediate",
        "instructions": [
            "Lie on your back with legs extended and hands tucked under your glutes or at your sides for lower back support.",
            "Press your lower back into the floor and lift both feet about 6 inches off the ground.",
            "Alternately kick your legs up and down in a small, controlled scissor motion, about 12 inches of range.",
            "Keep your legs as straight as possible and your core fully engaged throughout the movement.",
            "Breathe steadily and avoid holding your breath. One kick with each leg counts as one rep."
        ],
        "defaultReps": 20, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_034", "name": "Heel Touches",
        "description": "A beginner-friendly oblique exercise that involves reaching alternately toward each heel while lying on your back with knees bent.",
        "muscleGroups": ["core"], "difficulty": "beginner",
        "instructions": [
            "Lie on your back with knees bent and feet flat on the floor, about 6 inches from your glutes.",
            "Lift your shoulders slightly off the floor and extend your arms alongside your body.",
            "Reach your right hand down to touch your right heel by crunching and laterally flexing your torso.",
            "Return to center and reach your left hand toward your left heel.",
            "Continue alternating sides in a rhythmic motion. Keep your lower back pressed into the floor."
        ],
        "defaultReps": 15, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_035", "name": "V-Ups",
        "description": "An advanced abdominal exercise that simultaneously lifts the torso and legs to form a V shape, targeting the entire rectus abdominis.",
        "muscleGroups": ["core"], "difficulty": "advanced",
        "instructions": [
            "Lie flat on your back with arms extended overhead and legs straight on the floor.",
            "Simultaneously lift your torso and legs off the ground, reaching your hands toward your toes.",
            "Your body should form a V shape at the top of the movement, balancing on your sit bones.",
            "Lower both your upper body and legs back to the floor with control — do not slam down.",
            "If the full V-up is too challenging, bend your knees or perform one side at a time."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 45, "isTimeBased": False
    },
    {
        "id": "ex_036", "name": "Standing Oblique Crunch",
        "description": "A standing core exercise that targets the obliques by drawing the elbow toward the lifted knee, ideal for those who find floor exercises uncomfortable.",
        "muscleGroups": ["core"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet hip-width apart and place your fingertips behind your ears with elbows flared out.",
            "Shift your weight to your left foot and lift your right knee up and out to the side.",
            "Simultaneously crunch your right elbow down toward your right knee, contracting your right oblique.",
            "Return to the starting position with control and repeat for the prescribed reps.",
            "Complete all reps on one side before switching to the other. Keep the movement slow and controlled."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    # ── Back (37-39) ───────────────────────────────────────
    {
        "id": "ex_037", "name": "Superman",
        "description": "A posterior chain exercise performed face-down that strengthens the lower back, glutes, and rear shoulders through a back extension hold.",
        "muscleGroups": ["back", "glutes"], "difficulty": "beginner",
        "instructions": [
            "Lie face-down on the floor with arms extended straight overhead and legs fully extended.",
            "Simultaneously lift your arms, chest, and legs off the floor by contracting your back muscles and glutes.",
            "Hold the elevated position for 2-3 seconds, squeezing your back and glutes at the top.",
            "Slowly lower your arms, chest, and legs back to the starting position.",
            "Keep your neck in a neutral position by looking at the floor rather than craning your head up."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_038", "name": "Reverse Snow Angels",
        "description": "A back and rear deltoid exercise performed face-down, mimicking a snow angel motion to strengthen the muscles between the shoulder blades.",
        "muscleGroups": ["back", "shoulders"], "difficulty": "beginner",
        "instructions": [
            "Lie face-down with your forehead resting on the floor and arms extended at your sides, palms facing down.",
            "Lift your arms, chest, and head slightly off the floor, engaging your back muscles.",
            "Slowly sweep your arms in a wide arc from your sides to overhead, keeping them lifted the entire time.",
            "Reverse the motion, sweeping your arms back down to your sides in a controlled arc.",
            "Keep your arms straight and maintain the lifted position throughout. One full sweep up and back counts as one rep."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_039", "name": "Prone Y Raise",
        "description": "A face-down exercise that targets the lower trapezius and rear deltoids by raising the arms into a Y position overhead.",
        "muscleGroups": ["back", "shoulders"], "difficulty": "beginner",
        "instructions": [
            "Lie face-down with forehead on the floor and arms extended at a 45-degree angle above your head, forming a Y shape.",
            "With thumbs pointing toward the ceiling, lift both arms as high off the floor as you can.",
            "Squeeze your shoulder blades together at the top of the movement and hold for 1-2 seconds.",
            "Slowly lower your arms back to the floor with control.",
            "Focus on feeling the contraction between your shoulder blades. Keep your neck neutral throughout."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    # ── Glutes (40-44) ────────────────────────────────────
    {
        "id": "ex_040", "name": "Glute Bridge",
        "description": "A foundational glute activation exercise that targets the gluteus maximus and hamstrings while improving hip extension and posterior chain strength.",
        "muscleGroups": ["glutes", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Lie on your back with knees bent, feet flat on the floor about hip-width apart and close to your glutes.",
            "Press your lower back into the floor and engage your core.",
            "Drive through your heels to lift your hips toward the ceiling until your body forms a straight line from shoulders to knees.",
            "Squeeze your glutes hard at the top for 1-2 seconds, avoiding overextension of the lower back.",
            "Slowly lower your hips back to the floor with control and repeat."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_041", "name": "Single-Leg Glute Bridge",
        "description": "A unilateral glute bridge variation that addresses muscle imbalances and increases glute activation by working one side at a time.",
        "muscleGroups": ["glutes", "hamstrings"], "difficulty": "intermediate",
        "instructions": [
            "Lie on your back with knees bent and feet flat. Extend one leg straight up toward the ceiling or hold it above your hips.",
            "Press through the heel of your grounded foot and lift your hips until your body forms a straight line from shoulders to the grounded knee.",
            "Squeeze your glute hard at the top of the movement for 1-2 seconds.",
            "Lower your hips back to the floor with control, keeping the non-working leg elevated throughout.",
            "Complete all reps on one side before switching to the other leg."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_042", "name": "Donkey Kicks",
        "description": "A glute isolation exercise performed on hands and knees that targets the gluteus maximus through hip extension.",
        "muscleGroups": ["glutes"], "difficulty": "beginner",
        "instructions": [
            "Start on your hands and knees in a tabletop position with wrists under shoulders and knees under hips.",
            "Keeping your right knee bent at 90 degrees, lift your right leg behind you, driving your heel toward the ceiling.",
            "Raise your thigh until it is parallel with the floor, squeezing your glute at the top.",
            "Lower your knee back to the starting position with control, stopping just before it touches the floor.",
            "Complete all reps on one side before switching. Keep your core tight and avoid arching your lower back."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_043", "name": "Fire Hydrants",
        "description": "A hip abduction exercise performed on hands and knees that targets the gluteus medius and improves hip mobility and stability.",
        "muscleGroups": ["glutes"], "difficulty": "beginner",
        "instructions": [
            "Begin on your hands and knees in a tabletop position with a neutral spine.",
            "Keeping your right knee bent at 90 degrees, lift your right knee out to the side, away from your body.",
            "Raise your knee to hip height or as high as comfortable, maintaining the 90-degree bend.",
            "Hold briefly at the top, then lower your knee back to the starting position with control.",
            "Complete all reps on one side before switching. Keep your torso stable and avoid shifting your weight."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_044", "name": "Glute Kickbacks",
        "description": "A standing or kneeling glute exercise that isolates the gluteus maximus through a controlled backward leg extension.",
        "muscleGroups": ["glutes", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Stand facing a wall or chair for balance, or remain on your hands and knees.",
            "Keeping your right leg straight, slowly extend it directly behind you, squeezing your glute.",
            "Lift your leg until you feel a strong contraction in your glute without arching your lower back.",
            "Hold the top position for 1 second, then slowly lower your leg back to the starting position.",
            "Complete all reps on one side before switching. Focus on a slow, controlled tempo."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    # ── Cardio (45-51) ─────────────────────────────────────
    {
        "id": "ex_045", "name": "Jumping Jacks",
        "description": "A classic full-body cardiovascular exercise that elevates heart rate while working the shoulders, calves, and hip abductors.",
        "muscleGroups": ["cardio", "full_body"], "difficulty": "beginner",
        "instructions": [
            "Stand tall with feet together and arms at your sides.",
            "Jump your feet out wider than hip-width while simultaneously raising your arms overhead.",
            "Immediately jump your feet back together while lowering your arms to your sides.",
            "Land softly on the balls of your feet and maintain a slight bend in your knees throughout.",
            "Keep a steady, rhythmic pace and breathe naturally. Each jump out and back counts as one rep."
        ],
        "defaultReps": 20, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_046", "name": "High Knees",
        "description": "A high-intensity cardio exercise that involves running in place while driving knees up to hip height, building core and leg endurance.",
        "muscleGroups": ["cardio", "core"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet hip-width apart and arms at your sides.",
            "Quickly drive your right knee up toward your chest, at least to hip height.",
            "As you lower your right foot, immediately drive your left knee up to the same height.",
            "Pump your arms in opposition to your legs, as if sprinting in place.",
            "Stay on the balls of your feet and maintain an upright posture. Aim for a quick, rhythmic pace."
        ],
        "defaultReps": 20, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_047", "name": "Butt Kicks",
        "description": "A cardio exercise involving jogging in place while kicking heels up to the glutes, warming up hamstrings and elevating heart rate.",
        "muscleGroups": ["cardio", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet hip-width apart and arms at your sides.",
            "Jog in place and kick your right heel up toward your glutes behind you.",
            "Quickly switch to kick your left heel up toward your glutes.",
            "Keep your thighs relatively stationary — the motion should come from bending your knees behind you.",
            "Pump your arms naturally and maintain a steady, quick cadence. Each kick counts as one rep."
        ],
        "defaultReps": 20, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_048", "name": "Burpees",
        "description": "A challenging full-body conditioning exercise that combines a squat, plank, push-up, and jump into one fluid movement for maximum calorie burn.",
        "muscleGroups": ["full_body", "cardio"], "difficulty": "intermediate",
        "instructions": [
            "Stand with feet shoulder-width apart, then squat down and place your hands on the floor in front of you.",
            "Jump or step your feet back into a high plank position.",
            "Perform a push-up, lowering your chest to the floor and pressing back up (optional for beginners).",
            "Jump or step your feet forward toward your hands, returning to the squat position.",
            "Explosively jump upward, reaching your arms overhead. Land softly and immediately begin the next rep."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 60, "isTimeBased": False
    },
    {
        "id": "ex_049", "name": "Shadow Boxing",
        "description": "A standing cardio exercise that mimics boxing combinations, building shoulder endurance, coordination, and cardiovascular fitness.",
        "muscleGroups": ["cardio", "shoulders"], "difficulty": "beginner",
        "instructions": [
            "Stand in a staggered stance with your non-dominant foot forward, fists raised in a guard position by your chin.",
            "Throw jabs with your lead hand, fully extending your arm and rotating your fist, then pull it back quickly.",
            "Follow with a cross from your rear hand, rotating your hips and shoulders into the punch.",
            "Mix in hooks and uppercuts, keeping your core engaged and your feet light and mobile.",
            "Maintain constant movement, bouncing lightly on the balls of your feet. Throw 30-40 punches per set."
        ],
        "defaultReps": 30, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_050", "name": "Skater Jumps",
        "description": "A lateral plyometric exercise that builds single-leg power, agility, and cardiovascular endurance while targeting the glutes and outer thighs.",
        "muscleGroups": ["cardio", "glutes", "quads"], "difficulty": "intermediate",
        "instructions": [
            "Stand on your right foot with your left foot lifted behind you and your right knee slightly bent.",
            "Push off your right foot and leap laterally to the left, landing softly on your left foot.",
            "Swing your right foot behind your left ankle and reach your right hand toward your left foot for balance.",
            "Immediately push off your left foot and leap back to the right, landing on your right foot.",
            "Continue alternating side to side in a skating motion. Focus on soft, controlled landings."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_051", "name": "Tuck Jumps",
        "description": "An advanced plyometric exercise that develops explosive lower body power by jumping and tucking the knees to the chest at the peak.",
        "muscleGroups": ["cardio", "quads"], "difficulty": "advanced",
        "instructions": [
            "Stand with feet shoulder-width apart, knees slightly bent, arms at your sides.",
            "Bend your knees and explosively jump as high as you can.",
            "At the peak of your jump, tuck your knees up toward your chest and briefly touch your knees with your hands.",
            "Extend your legs back down and land softly on the balls of your feet with bent knees to absorb impact.",
            "Reset quickly and repeat. Focus on maximum height and soft, quiet landings."
        ],
        "defaultReps": 8, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 60, "isTimeBased": False
    },
    # ── Flexibility (52-58) ────────────────────────────────
    {
        "id": "ex_052", "name": "Hamstring Stretch",
        "description": "A seated or standing stretch that targets the hamstrings to improve posterior chain flexibility and reduce risk of lower back tightness.",
        "muscleGroups": ["flexibility", "hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Sit on the floor with your right leg extended straight in front and your left foot placed against your right inner thigh.",
            "Sit up tall and hinge forward at your hips, reaching your hands toward your right foot.",
            "Reach as far as comfortable until you feel a gentle stretch along the back of your right thigh.",
            "Hold the stretch while breathing deeply, relaxing further into it with each exhale.",
            "Switch legs after completing the hold on one side. Never bounce or force the stretch."
        ],
        "defaultReps": None, "defaultSets": 2, "defaultHoldSeconds": 30, "restSeconds": 10, "isTimeBased": True
    },
    {
        "id": "ex_053", "name": "Quad Stretch",
        "description": "A standing stretch that targets the quadriceps and hip flexors, improving anterior thigh flexibility and reducing knee strain.",
        "muscleGroups": ["flexibility", "quads"], "difficulty": "beginner",
        "instructions": [
            "Stand tall near a wall or chair for balance support if needed.",
            "Bend your right knee and grab your right ankle or foot with your right hand behind you.",
            "Gently pull your heel toward your glute until you feel a stretch along the front of your thigh.",
            "Keep your knees close together and your standing leg slightly bent for stability.",
            "Hold the stretch while keeping an upright posture, then switch legs."
        ],
        "defaultReps": None, "defaultSets": 2, "defaultHoldSeconds": 30, "restSeconds": 10, "isTimeBased": True
    },
    {
        "id": "ex_054", "name": "Hip Flexor Stretch",
        "description": "A kneeling lunge stretch that opens up tight hip flexors, which are commonly shortened from prolonged sitting.",
        "muscleGroups": ["flexibility"], "difficulty": "beginner",
        "instructions": [
            "Kneel on your right knee with your left foot flat on the floor in front of you, left knee at 90 degrees.",
            "Place your hands on your left thigh or raise them overhead for a deeper stretch.",
            "Gently push your hips forward until you feel a stretch in the front of your right hip.",
            "Keep your torso upright and your core engaged to protect your lower back.",
            "Hold the stretch for the prescribed time, breathing deeply, then switch sides."
        ],
        "defaultReps": None, "defaultSets": 2, "defaultHoldSeconds": 30, "restSeconds": 10, "isTimeBased": True
    },
    {
        "id": "ex_055", "name": "Cat-Cow",
        "description": "A gentle spinal mobilization exercise that alternates between spinal flexion and extension, improving back flexibility and relieving tension.",
        "muscleGroups": ["flexibility", "core"], "difficulty": "beginner",
        "instructions": [
            "Start on hands and knees in a tabletop position with wrists under shoulders and knees under hips.",
            "Cow: Inhale and drop your belly toward the floor, lifting your chest and tailbone toward the ceiling, gently arching your back.",
            "Cat: Exhale and round your spine toward the ceiling, tucking your chin to your chest and drawing your belly button toward your spine.",
            "Flow smoothly between the two positions, synchronizing each movement with your breath.",
            "Perform the prescribed number of reps slowly, spending about 3 seconds in each position."
        ],
        "defaultReps": 10, "defaultSets": 2, "defaultHoldSeconds": None, "restSeconds": 10, "isTimeBased": False
    },
    {
        "id": "ex_056", "name": "Child's Pose",
        "description": "A restorative yoga position that gently stretches the hips, back, and shoulders while promoting relaxation and deep breathing.",
        "muscleGroups": ["flexibility"], "difficulty": "beginner",
        "instructions": [
            "Kneel on the floor and sit back on your heels with your big toes touching and knees spread apart.",
            "Slowly fold forward, walking your hands out in front of you and lowering your chest toward the floor.",
            "Rest your forehead on the floor and extend your arms straight ahead or along your sides.",
            "Relax your shoulders away from your ears and breathe deeply into your lower back.",
            "Hold the position for the prescribed duration, sinking deeper with each exhale."
        ],
        "defaultReps": None, "defaultSets": 2, "defaultHoldSeconds": 30, "restSeconds": 10, "isTimeBased": True
    },
    {
        "id": "ex_057", "name": "Hip Circles",
        "description": "A dynamic hip mobility exercise that warms up and loosens the hip joints through controlled circular motions.",
        "muscleGroups": ["flexibility", "glutes"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet hip-width apart and hands on your hips.",
            "Push your hips forward, then circle them to the right, back, left, and forward again in a smooth, large circle.",
            "Perform the prescribed reps in one direction, then reverse the direction for the same number of reps.",
            "Keep your upper body as still as possible — the motion should be isolated to your hips.",
            "Make the circles as large and smooth as possible, exploring your full range of motion."
        ],
        "defaultReps": 10, "defaultSets": 2, "defaultHoldSeconds": None, "restSeconds": 10, "isTimeBased": False
    },
    {
        "id": "ex_058", "name": "Plank to Downward Dog",
        "description": "A flowing transition between high plank and downward dog positions that stretches the calves, hamstrings, and shoulders while engaging the core.",
        "muscleGroups": ["flexibility", "core", "shoulders"], "difficulty": "beginner",
        "instructions": [
            "Begin in a high plank position with hands under shoulders and body in a straight line.",
            "Push your hips up and back, pressing your chest toward your thighs to move into a downward dog position.",
            "In downward dog, press your heels toward the floor and straighten your legs as much as comfortable.",
            "Hold the downward dog for a breath, then shift forward back into the high plank position.",
            "Flow smoothly between the two positions for the prescribed reps, coordinating movement with breath."
        ],
        "defaultReps": 8, "defaultSets": 2, "defaultHoldSeconds": None, "restSeconds": 15, "isTimeBased": False
    },
    # ── Other (59-65) ──────────────────────────────────────
    {
        "id": "ex_059", "name": "Tricep Dips",
        "description": "An upper body exercise using a chair or step edge to target the triceps, chest, and anterior shoulders through elbow extension.",
        "muscleGroups": ["triceps", "chest"], "difficulty": "intermediate",
        "instructions": [
            "Sit on the edge of a sturdy chair or step and place your hands beside your hips, fingers gripping the edge.",
            "Walk your feet forward and slide your hips off the edge so your weight is supported by your arms.",
            "Lower your body by bending your elbows to approximately 90 degrees, keeping your back close to the edge.",
            "Press through your palms to straighten your arms and return to the starting position.",
            "Keep your shoulders down and away from your ears. To make it easier, keep your knees bent; to make it harder, extend your legs straight."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_060", "name": "Calf Raises",
        "description": "A simple but effective exercise for strengthening and toning the calf muscles through controlled heel raises.",
        "muscleGroups": ["calves"], "difficulty": "beginner",
        "instructions": [
            "Stand with feet hip-width apart near a wall or chair for light balance support if needed.",
            "Slowly rise up onto the balls of your feet, lifting your heels as high as possible.",
            "Squeeze your calf muscles at the top and hold for 1-2 seconds.",
            "Slowly lower your heels back to the floor with a controlled 2-3 second descent.",
            "For increased range of motion, perform on a step with heels hanging off the edge."
        ],
        "defaultReps": 15, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_061", "name": "Single-Leg Calf Raise",
        "description": "A unilateral calf exercise that doubles the resistance on each leg, improving calf strength and correcting bilateral imbalances.",
        "muscleGroups": ["calves"], "difficulty": "intermediate",
        "instructions": [
            "Stand on your right foot near a wall or chair for balance, with your left foot lifted behind you.",
            "Slowly rise up onto the ball of your right foot, lifting your heel as high as possible.",
            "Hold the top position for 1-2 seconds, squeezing your calf muscle.",
            "Lower your heel back down slowly with a controlled tempo.",
            "Complete all reps on one leg before switching to the other."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
    {
        "id": "ex_062", "name": "Step-Ups",
        "description": "A functional lower body exercise using a stair or step that builds quad and glute strength while improving single-leg stability.",
        "muscleGroups": ["quads", "glutes", "calves"], "difficulty": "beginner",
        "instructions": [
            "Stand facing a sturdy step, stair, or low bench with feet hip-width apart.",
            "Place your right foot fully on the step, ensuring your entire foot is on the surface.",
            "Press through your right heel to drive your body up onto the step, bringing your left foot up to meet it.",
            "Step back down with your left foot first, followed by your right foot, returning to the starting position.",
            "Alternate leading legs each rep or complete all reps on one side before switching."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_063", "name": "Inchworms",
        "description": "A dynamic full-body exercise that walks the hands out to a plank and back, stretching the hamstrings and engaging the core and shoulders.",
        "muscleGroups": ["full_body", "core", "flexibility"], "difficulty": "intermediate",
        "instructions": [
            "Stand with feet hip-width apart and hinge at the hips to place your hands on the floor in front of your feet.",
            "Keeping your legs as straight as possible, walk your hands forward one at a time until you reach a high plank position.",
            "Hold the plank briefly, ensuring your body is in a straight line.",
            "Walk your hands back toward your feet, keeping your legs as straight as comfortable.",
            "Stand up tall and repeat. Each full walk out and back counts as one rep."
        ],
        "defaultReps": 6, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_064", "name": "Bear Crawl",
        "description": "A full-body movement that builds core stability, shoulder endurance, and coordination by crawling forward on hands and feet with knees hovering.",
        "muscleGroups": ["full_body", "core", "shoulders"], "difficulty": "intermediate",
        "instructions": [
            "Start on your hands and knees, then lift your knees about 2 inches off the floor. This is your starting position.",
            "Move your right hand and left foot forward simultaneously, then your left hand and right foot.",
            "Keep your back flat and hips low throughout the crawl — avoid letting your hips rise up.",
            "Crawl forward for the prescribed distance or reps, then crawl backward to the starting position.",
            "Take small, controlled steps and focus on keeping your core braced throughout the movement."
        ],
        "defaultReps": 10, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 30, "isTimeBased": False
    },
    {
        "id": "ex_065", "name": "Lying Leg Curl",
        "description": "A bodyweight hamstring exercise performed lying face down, using heel-to-glute curls to isolate the hamstrings without equipment.",
        "muscleGroups": ["hamstrings"], "difficulty": "beginner",
        "instructions": [
            "Lie face-down on the floor with your legs extended and arms folded under your forehead for comfort.",
            "Keeping your hips pressed into the floor, slowly curl your right heel toward your glute by bending your knee.",
            "Squeeze your hamstring at the top of the movement and hold for 1 second.",
            "Slowly lower your leg back to the starting position with control.",
            "Alternate legs each rep or complete all reps on one side before switching. Focus on slow, deliberate contractions."
        ],
        "defaultReps": 12, "defaultSets": 3, "defaultHoldSeconds": None, "restSeconds": 20, "isTimeBased": False
    },
]

# ─────────────────────────────────────────────────────────────
# ROUTINE GENERATION
# ─────────────────────────────────────────────────────────────

# Helper: build exercise reference dict by exerciseId (1-based)
ex_by_id = {i + 1: e for i, e in enumerate(exercises)}
ex_name = {i + 1: e["name"] for i, e in enumerate(exercises)}

# Week progression parameters
# (reps_multiplier, sets, hold_multiplier)
WEEK_PROG = {
    "beginner": [
        {"reps_factor": 1.0,  "sets_base": 2, "hold_s": 15},  # week 1
        {"reps_factor": 1.25, "sets_base": 2, "hold_s": 20},  # week 2
        {"reps_factor": 1.25, "sets_base": 3, "hold_s": 25},  # week 3
        {"reps_factor": 1.5,  "sets_base": 3, "hold_s": 30},  # week 4
        {"reps_factor": 1.5,  "sets_base": 3, "hold_s": 30},  # days 29-30
    ],
    "intermediate": [
        {"reps_factor": 1.0,  "sets_base": 3, "hold_s": 30},
        {"reps_factor": 1.0,  "sets_base": 4, "hold_s": 35},
        {"reps_factor": 1.25, "sets_base": 3, "hold_s": 40},
        {"reps_factor": 1.25, "sets_base": 4, "hold_s": 45},
        {"reps_factor": 1.25, "sets_base": 4, "hold_s": 45},
    ],
    "advanced": [
        {"reps_factor": 1.0,  "sets_base": 4, "hold_s": 45},
        {"reps_factor": 1.2,  "sets_base": 4, "hold_s": 50},
        {"reps_factor": 1.2,  "sets_base": 5, "hold_s": 55},
        {"reps_factor": 1.33, "sets_base": 5, "hold_s": 60},
        {"reps_factor": 1.33, "sets_base": 5, "hold_s": 60},
    ],
}

# Base reps per difficulty
BASE_REPS = {
    "beginner": {
        # push-ups
        1: 8, 2: 8, 3: 6, 4: 10, 5: 6, 6: 6, 7: 6, 8: 4,
        # squats
        9: 10, 10: 10, 11: 8, 12: 3, 14: 12,
        # lunges
        16: 8, 17: 8, 18: 8, 19: 8,
        # planks (non-timed reps)
        23: 6, 24: 8,
        # core
        26: 12, 27: 10, 28: 8, 29: 15, 30: 8, 31: 8, 32: 10, 33: 15, 34: 12, 35: 5, 36: 10,
        # back
        37: 8, 38: 6, 39: 8,
        # glutes
        40: 10, 41: 8, 42: 10, 43: 10, 44: 10,
        # cardio
        45: 15, 46: 15, 47: 15, 48: 5, 49: 20, 50: 8, 51: 5,
        # flexibility (reps-based)
        55: 8, 57: 8, 58: 6,
        # other
        59: 8, 60: 12, 61: 10, 62: 8, 63: 5, 64: 8, 65: 10,
    },
    "intermediate": {
        1: 12, 2: 12, 3: 10, 4: 15, 5: 10, 6: 10, 7: 10, 8: 6,
        9: 15, 10: 15, 11: 12, 12: 5, 14: 15,
        16: 12, 17: 12, 18: 12, 19: 10,
        23: 8, 24: 12,
        26: 15, 27: 15, 28: 12, 29: 20, 30: 12, 31: 12, 32: 15, 33: 20, 34: 15, 35: 8, 36: 12,
        37: 12, 38: 10, 39: 12,
        40: 15, 41: 10, 42: 15, 43: 15, 44: 12,
        45: 25, 46: 20, 47: 20, 48: 8, 49: 30, 50: 12, 51: 8,
        55: 10, 57: 10, 58: 8,
        59: 12, 60: 15, 61: 12, 62: 12, 63: 8, 64: 10, 65: 12,
    },
    "advanced": {
        1: 15, 2: 15, 3: 12, 4: 18, 5: 12, 6: 12, 7: 12, 8: 10,
        9: 20, 10: 20, 11: 15, 12: 8, 14: 20,
        16: 15, 17: 15, 18: 15, 19: 12,
        23: 10, 24: 15,
        26: 20, 27: 20, 28: 15, 29: 25, 30: 15, 31: 15, 32: 20, 33: 25, 34: 20, 35: 12, 36: 15,
        37: 15, 38: 12, 39: 15,
        40: 20, 41: 12, 42: 20, 43: 20, 44: 15,
        45: 30, 46: 25, 47: 25, 48: 12, 49: 40, 50: 15, 51: 10,
        55: 12, 57: 12, 58: 10,
        59: 15, 60: 20, 61: 15, 62: 15, 63: 10, 64: 12, 65: 15,
    },
}

TIME_BASED_IDS = {13, 15, 20, 21, 22, 25, 52, 53, 54, 56}

def get_week(day):
    if day <= 7: return 0
    if day <= 14: return 1
    if day <= 21: return 2
    if day <= 28: return 3
    return 4

def build_exercise_entry(ex_id, diff, day):
    week = get_week(day)
    prog = WEEK_PROG[diff][week]
    is_timed = ex_id in TIME_BASED_IDS

    if is_timed:
        return {
            "exerciseId": ex_id,
            "exerciseName": ex_name[ex_id],
            "reps": None,
            "sets": prog["sets_base"],
            "holdSeconds": prog["hold_s"]
        }
    else:
        base = BASE_REPS[diff].get(ex_id, 10)
        reps = round(base * prog["reps_factor"])
        return {
            "exerciseId": ex_id,
            "exerciseName": ex_name[ex_id],
            "reps": reps,
            "sets": prog["sets_base"],
            "holdSeconds": None
        }


# ── Day templates: exercise IDs per day-type per difficulty ──

DAY_TEMPLATES = {
    # Day type 1: Push + Core
    "push_core": {
        "beginner":     [[4, 1, 20, 26, 31],       [1, 4, 20, 26, 30, 31],       [1, 2, 20, 27, 30, 34],       [1, 2, 22, 20, 27, 28]],
        "intermediate": [[1, 2, 3, 20, 27, 28],     [1, 5, 3, 21, 27, 33],        [2, 3, 5, 21, 28, 32],        [1, 3, 5, 6, 21, 28, 33]],
        "advanced":     [[3, 5, 6, 8, 20, 35, 33],  [3, 5, 8, 7, 21, 35, 33],     [5, 6, 8, 7, 21, 35, 32],     [3, 5, 6, 8, 7, 25, 35, 33]],
    },
    # Day type 2: Legs + Glutes
    "legs_glutes": {
        "beginner":     [[9, 16, 40, 42, 43],       [9, 16, 40, 42, 43, 44],      [9, 10, 17, 40, 42, 43],      [9, 10, 16, 17, 40, 42, 43]],
        "intermediate": [[10, 11, 17, 41, 42, 19],  [10, 11, 16, 41, 42, 44],     [9, 11, 19, 41, 43, 44],      [10, 11, 17, 19, 41, 42, 44]],
        "advanced":     [[11, 12, 18, 19, 41, 44],  [11, 12, 18, 19, 41, 42, 44], [11, 12, 17, 19, 41, 43, 44], [11, 12, 18, 19, 14, 41, 42, 44]],
    },
    # Day type 3: Pull + Core
    "pull_core": {
        "beginner":     [[37, 38, 39, 30, 34],       [37, 38, 39, 30, 34, 31],     [37, 38, 39, 26, 30, 36],     [37, 38, 39, 27, 30, 31, 36]],
        "intermediate": [[37, 38, 39, 32, 29, 24],   [37, 38, 39, 32, 33, 24],     [37, 38, 39, 32, 28, 23],     [37, 38, 39, 28, 32, 24, 33]],
        "advanced":     [[37, 38, 39, 35, 32, 24, 64],[37, 38, 39, 35, 33, 24, 64],[37, 38, 39, 35, 28, 23, 64], [37, 38, 39, 35, 32, 24, 23, 64]],
    },
    # Day type 4: Cardio + Flexibility
    "cardio_flex": {
        "beginner":     [[45, 46, 47, 52, 53, 54],         [45, 46, 47, 49, 52, 54],          [45, 46, 47, 49, 52, 53, 54],    [45, 46, 47, 49, 29, 52, 53, 54]],
        "intermediate": [[45, 46, 29, 49, 50, 52, 54],     [45, 46, 50, 49, 48, 52, 54],      [46, 48, 50, 49, 29, 52, 54, 53],[46, 48, 50, 49, 29, 11, 52, 54]],
        "advanced":     [[48, 51, 29, 50, 49, 46, 54],     [48, 51, 50, 46, 49, 29, 54, 52],  [48, 51, 50, 46, 49, 11, 54, 52],[48, 51, 50, 46, 49, 29, 11, 54, 52]],
    },
    # Day type 5: Push + Triceps
    "push_tri": {
        "beginner":     [[1, 4, 59, 20],            [1, 4, 59, 22],                [1, 2, 59, 20, 22],          [1, 2, 4, 59, 20]],
        "intermediate": [[3, 2, 6, 59, 22],         [3, 1, 6, 59, 21],             [3, 5, 6, 59, 7, 22],       [3, 5, 6, 7, 59, 25]],
        "advanced":     [[3, 7, 6, 8, 59, 25],      [3, 7, 5, 8, 59, 25],          [3, 7, 6, 5, 8, 59, 25],    [3, 7, 6, 5, 8, 59, 25, 23]],
    },
    # Day type 6: Legs + Calves
    "legs_calves": {
        "beginner":     [[9, 17, 13, 60, 62],       [9, 17, 13, 60, 62, 65],       [9, 10, 17, 13, 60, 62],    [9, 10, 16, 17, 13, 60, 62]],
        "intermediate": [[10, 18, 11, 61, 14, 62],  [10, 18, 11, 61, 62, 65],      [10, 19, 11, 61, 14, 62],   [10, 18, 11, 19, 61, 14, 62]],
        "advanced":     [[11, 12, 19, 14, 61, 51],  [11, 12, 19, 18, 61, 14],      [11, 12, 19, 18, 14, 61, 51],[11, 12, 19, 18, 14, 61, 51, 62]],
    },
    # Day type 7: Active Recovery (Flexibility + Light Core)
    "recovery": {
        "beginner":     [[55, 56, 52, 53, 54, 57],         [55, 56, 52, 53, 54, 57, 58],      [55, 56, 52, 53, 54, 57, 58, 30],[55, 56, 52, 53, 54, 57, 58, 30, 31]],
        "intermediate": [[55, 56, 58, 52, 54, 30, 57],     [55, 56, 58, 52, 53, 54, 30, 57],  [55, 56, 58, 63, 52, 54, 30, 57],[55, 56, 58, 63, 52, 53, 54, 30, 31, 57]],
        "advanced":     [[55, 56, 58, 63, 52, 54, 30, 57], [55, 56, 58, 63, 52, 53, 54, 30, 57],[55, 56, 58, 63, 52, 53, 54, 30, 31, 57],[55, 56, 58, 63, 52, 53, 54, 30, 31, 36, 57]],
    },
    # Days 29-30: Full Body
    "fullbody": {
        "beginner":     [[1, 9, 40, 37, 20, 45, 55],                [1, 9, 16, 40, 37, 20, 45, 26, 55]],
        "intermediate": [[3, 11, 41, 37, 21, 48, 29, 55],           [3, 11, 17, 41, 37, 21, 48, 32, 55, 50]],
        "advanced":     [[6, 8, 12, 41, 37, 25, 48, 51, 35, 55],    [6, 8, 12, 19, 41, 37, 25, 48, 51, 35, 64, 55]],
    },
}

# Day-of-week mapping (1-indexed day → day type)
def day_type(day):
    if day >= 29:
        return "fullbody"
    d = ((day - 1) % 7)  # 0..6
    return ["push_core", "legs_glutes", "pull_core", "cardio_flex", "push_tri", "legs_calves", "recovery"][d]

DAY_CATEGORY = {
    "push_core": "push",
    "legs_glutes": "legs",
    "pull_core": "pull",
    "cardio_flex": "cardio",
    "push_tri": "push",
    "legs_calves": "legs",
    "recovery": "flexibility",
    "fullbody": "fullbody",
}

DAY_TAGS = {
    "push_core":   ["upper-body", "core"],
    "legs_glutes": ["lower-body", "glutes"],
    "pull_core":   ["back", "core"],
    "cardio_flex": ["cardio", "flexibility"],
    "push_tri":    ["upper-body", "triceps"],
    "legs_calves": ["lower-body", "calves"],
    "recovery":    ["flexibility", "recovery"],
    "fullbody":    ["full-body", "assessment"],
}

DAY_TITLE_FOCUS = {
    "push_core":   "Push & Core",
    "legs_glutes": "Legs & Glutes",
    "pull_core":   "Pull & Core",
    "cardio_flex": "Cardio & Flexibility",
    "push_tri":    "Push & Triceps",
    "legs_calves": "Legs & Calves",
    "recovery":    "Active Recovery",
    "fullbody":    "Full Body",
}

DURATION = {
    "beginner":     {"push_core": 20, "legs_glutes": 22, "pull_core": 20, "cardio_flex": 25, "push_tri": 18, "legs_calves": 22, "recovery": 20, "fullbody": 30},
    "intermediate": {"push_core": 30, "legs_glutes": 32, "pull_core": 28, "cardio_flex": 30, "push_tri": 28, "legs_calves": 30, "recovery": 25, "fullbody": 40},
    "advanced":     {"push_core": 40, "legs_glutes": 42, "pull_core": 38, "cardio_flex": 38, "push_tri": 38, "legs_calves": 40, "recovery": 30, "fullbody": 50},
}

DIFF_LABEL = {"beginner": "Beginner", "intermediate": "Intermediate", "advanced": "Advanced"}
DIFF_PREFIX = {"beginner": "b", "intermediate": "i", "advanced": "a"}

DAY_DESCRIPTIONS = {
    "push_core": [
        "Build upper body and core strength with foundational pushing movements.",
        "Strengthen your chest, shoulders, and midsection with progressive push patterns.",
        "Challenge your pressing muscles and deepen your core engagement.",
        "Push your upper body and core to new levels with increased volume.",
    ],
    "legs_glutes": [
        "Strengthen and tone your lower body with squats, lunges, and glute work.",
        "Build powerful legs and glutes with progressive lower body movements.",
        "Target your quads, hamstrings, and glutes with increased training volume.",
        "Peak lower body session challenging your legs and glutes with maximum effort.",
    ],
    "pull_core": [
        "Target your back muscles and core with posterior chain exercises.",
        "Build a strong back and resilient core with pulling and stabilization movements.",
        "Increase back endurance and core control with progressive volume.",
        "Challenge your posterior chain and core with peak-level pulling work.",
    ],
    "cardio_flex": [
        "Elevate your heart rate and improve flexibility with dynamic movements and stretches.",
        "Boost cardiovascular fitness and loosen tight muscles with varied cardio and mobility work.",
        "Build endurance and maintain flexibility with progressive cardio and stretching.",
        "Push your cardio capacity while maintaining full-body flexibility and mobility.",
    ],
    "push_tri": [
        "Focus on pressing movements and triceps isolation for defined arms and chest.",
        "Build pushing endurance and sculpt your triceps with targeted variations.",
        "Advance your pressing strength and tricep definition with higher volume.",
        "Peak push and tricep session for maximum upper body toning.",
    ],
    "legs_calves": [
        "Tone your legs and build calf strength with squats, lunges, and calf raises.",
        "Develop lower leg endurance and overall leg strength with progressive movements.",
        "Challenge your quads, hamstrings, and calves with increased work volume.",
        "Peak leg and calf session for maximum lower body muscle engagement.",
    ],
    "recovery": [
        "Restore your body with gentle stretching, mobility work, and light core activation.",
        "Active recovery session focusing on flexibility, deep stretching, and relaxation.",
        "Promote muscle recovery and joint health with targeted mobility and gentle core work.",
        "Comprehensive recovery session combining deep stretches with light stabilization exercises.",
    ],
    "fullbody": [
        "A comprehensive full-body session testing your strength and endurance across all muscle groups.",
        "Full body assessment day — push, pull, squat, and core in one complete session.",
    ],
}


routines = []

for diff in ["beginner", "intermediate", "advanced"]:
    for day in range(1, 31):
        dt = day_type(day)
        week = get_week(day)

        # Select exercise list
        template = DAY_TEMPLATES[dt][diff]
        if dt == "fullbody":
            idx = 0 if day == 29 else 1
            ex_ids = template[idx]
        else:
            ex_ids = template[min(week, len(template) - 1)]

        # Build exercise entries
        ex_entries = [build_exercise_entry(eid, diff, day) for eid in ex_ids]

        # Determine description index
        if dt == "fullbody":
            desc = DAY_DESCRIPTIONS[dt][0 if day == 29 else 1]
        else:
            desc = DAY_DESCRIPTIONS[dt][min(week, len(DAY_DESCRIPTIONS[dt]) - 1)]

        # Duration increases slightly with weeks
        base_dur = DURATION[diff][dt]
        dur = base_dur + (week * 2)

        routine = {
            "id": f"routine_{DIFF_PREFIX[diff]}_{day:02d}",
            "title": f"Day {day}: {DAY_TITLE_FOCUS[dt]} ({DIFF_LABEL[diff]})",
            "description": desc,
            "category": DAY_CATEGORY[dt],
            "difficulty": diff,
            "durationMin": dur,
            "exercises": ex_entries,
            "dayOfProgram": day,
            "tags": DAY_TAGS[dt],
        }
        routines.append(routine)


# ─────────────────────────────────────────────────────────────
# OUTPUT
# ─────────────────────────────────────────────────────────────
output = {"exercises": exercises, "routines": routines}

out_path = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                        "src", "main", "resources", "seed_workouts.json")
os.makedirs(os.path.dirname(out_path), exist_ok=True)

with open(out_path, "w", encoding="utf-8") as f:
    json.dump(output, f, indent=2, ensure_ascii=False)

print(f"Generated {len(exercises)} exercises and {len(routines)} routines.")
print(f"Output: {out_path}")
print(f"File size: {os.path.getsize(out_path) / 1024:.1f} KB")

# Quick validation
assert len(exercises) == 65, f"Expected 65 exercises, got {len(exercises)}"
assert len(routines) == 90, f"Expected 90 routines, got {len(routines)}"

# Check all exerciseIds in routines reference valid exercises
valid_ids = set(range(1, 66))
for r in routines:
    for e in r["exercises"]:
        assert e["exerciseId"] in valid_ids, f"Invalid exerciseId {e['exerciseId']} in routine {r['id']}"

# Check difficulties
for r in routines:
    assert r["difficulty"] in ("beginner", "intermediate", "advanced"), f"Bad difficulty in {r['id']}"

# Check categories
valid_cats = {"push", "pull", "legs", "core", "fullbody", "flexibility", "cardio"}
for r in routines:
    assert r["category"] in valid_cats, f"Bad category '{r['category']}' in {r['id']}"

# Check 30 days per difficulty
for diff in ["beginner", "intermediate", "advanced"]:
    diff_routines = [r for r in routines if r["difficulty"] == diff]
    assert len(diff_routines) == 30, f"Expected 30 routines for {diff}, got {len(diff_routines)}"
    days = sorted([r["dayOfProgram"] for r in diff_routines])
    assert days == list(range(1, 31)), f"Missing days for {diff}: expected 1-30, got {days}"

print("All validations passed!")
