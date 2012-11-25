~passive_specs = (
	// (minval, maxval, warp, step, default, units)
	wt_pos: ControlSpec(0, 1, \lin, 0.01, 0),
	velocity: \unipolar.asSpec,
	ktr: \unipolar.asSpec,
	pitch: ControlSpec(-64,64, \lin, 0, 0, "midi"),
	rate: \widefreq.asSpec,
	glidefade: \unipolar.asSpec,
	env: \attack.asSpec,
	amp: ControlSpec(0, 1, 'amp', 0, 0.1, ""),
	wideamp: ControlSpec(0, 3, 'amp', 0, 0.1, ""),
	crush: ControlSpec(1, 31, 'lin', 1, 1, ""),
	smalldelay: ControlSpec(0, 0.02, 'lin', 0, 0.001, ""),
	envamp: ControlSpec(0, 1, 'amp', 0, 1, "")
);


~make_module_kinds = {
	var module_kinds;
	var specs = ~passive_specs;

	module_kinds = (
		filter: [
			(
				name: "LPF",
				uname: \lpf,
				args: ["Cutoff"],
				specs: [\freq]
			),
			(
				name: "RLPF",
				uname: \rlpf,
				args: ["Cutoff", "Resonance"],
				specs: [\freq, \rq]
			),
			(
				name: "HPF",
				uname: \hpf,
				args: ["Cutoff"],
				specs: [\freq]
			),
			(
				name: "RHPF",
				uname: \rhpf,
				args: ["Cutoff", "Resonance"],
				specs: [\freq, \rq]
			),
			(
				name: "BPF",
				uname: \bpf,
				args: ["Cutoff", "Resonance"],
				specs: [\freq, \rq]
			),
			(
				name: "Comb",
				uname: \comb,
				args: ["Max Delay", "Delay", "Decay"],
				specs: [\delay, \delay, \decay]
			),
		],
		noise: [
			(
				name: "White noise",
				uname: \white,
				args: ["Cutoff"],
				specs: [\freq]
			),
			(
				name: "Pink noise",
				uname: \pink,
				args: ["Cutoff"],
				specs: [\freq]
			),
			(
				name: "Brown noise",
				uname: \brown,
				args: ["Cutoff"],
				specs: [\freq]
			)
		],
		insert: [
			(
				name: "Freqshift",
				uname: \freqshift,
				args: ["Wet/Dry", "Shift"],
				specs: [\unipolar, \freq]
			),
			(
				name: "Delay",
				uname: \simpledelay,
				args: ["Wet/Dry", "Delay"],
				specs: [\unipolar, \delay]
			),
			(
				name: "Hold",
				uname: \samplehold,
				args: ["Wet/Dry", "Pitch"],
				specs: [\unipolar, \widefreq]
			),
			(
				name: "Bitcrusher",
				uname: \bitcrusher,
				args: ["Wet/Dry", "Crush"],
				specs: [\unipolar, specs[\crush]]
			),
			(
				name: "Filter",
				uname: \simplefilter,
				args: ["HP freq", "LP freq"],
				specs: [\freq, \freq]
			),
			(
				name: "SineShaper",
				uname: \sinshaper,
				args: ["Wet/Dry", "Drive"],
				specs: [\unipolar, \unipolar]
			),
			(
				name: "ParaShaper",
				uname: \parashaper,
				args: ["Wet/Dry", "Drive"],
				specs: [\unipolar, \unipolar]
			),
			(
				name: "Hard clipper",
				uname: \hardclipper,
				args: ["Wet/Dry", "Drive"],
				specs: [\unipolar, \unipolar]
			)
		],
		fx: [
			(
				name: "Reverb",
				uname: \reverb,
				args: ["Mix", "Room", "Damp"],
				specs: [\unipolar, \unipolar, \unipolar]
			),
			(
				name: "Flanger",
				uname: \flanger,
				args: ["Mix", "Rate", "Feedback", "Depth"],
				specs: [\unipolar, \widefreq, \unipolar, specs[\smalldelay]]
			),
			(
				name: "Chorus",
				uname: \chorus,
				args: ["Mix", "Rate", "Offset", "Depth"],
				specs: [\unipolar, \widefreq, specs[\smalldelay], specs[\smalldelay]]
			),
			(
				name: "Phaser",
				uname: \phaser,
				args: ["Mix", "Rate", "Feedback", "Depth"],
				specs: [\unipolar, \widefreq, \unipolar, specs[\smalldelay]]
			),
			(
				name: "Delay",
				uname: \delay,
				args: ["Mix", "Damp", "Delay left", "Delay right"],
				specs: [\unipolar, \freq, specs[\smalldelay], specs[\smalldelay]]
			)
		],
		modulator: [
			(
				name: "LFO",
				uname: \lfo,
			),
			(
				name: "Performer",
				uname: \performer,
			),
			(
				name: "Stepper",
				uname: \stepper,
			)
		],
		routing_insert: [
			(
				name: "Before filter 1",
				uname: \before_filter1,
			),
			(
				name: "Before filter 2",
				uname: \before_filter2,
			),
			(
				name: "After filter 1",
				uname: \after_filter1,
			),
			(
				name: "After filter 2",
				uname: \after_filter2,
			),
			(
				name: "Between filters",
				uname: \between_filters,
			),
			(
				name: "Before pan",
				uname: \before_pan,
			),
			(
				name: "In feedback",
				uname: \in_feedback,
			)
		],
		routing_feedback: [
			(
				name: "After filter 1",
				uname: \after_filter1,
			),
			(
				name: "After filter 2",
				uname: \after_filter2,
			),
			(
				name: "Between filters",
				uname: \between_filters,
			),
			(
				name: "Before pan",
				uname: \before_pan,
			),
			(
				name: "After pan",
				uname: \after_pan,
			)
		],
		routing_bypass_osc: [
			(
				name: "No bypass",
				uname: \off,
			),
			(
				name: "Osc 1",
				uname: \osc1,
			),
			(
				name: "Osc 2",
				uname: \osc2,
			),
			(
				name: "Osc 3",
				uname: \osc3,
			)
		],
		routing_bypass_dest: [
			// TODO
			(
				name: "Before Fx 1",
				uname: \before_fx1,
			),
			(
				name: "Before Fx 2",
				uname: \before_fx2,
			),
			(
				name: "Before Eq",
				uname: \before_eq,
			)
		],
	);

	module_kinds;

};

~make_param_data = {
	var params = ();
	var specs = ~passive_specs;

	///////// osc params
	3.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("osc"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_pitch").asSymbol,
				name: "Pitch",
				kind: \knob,
				spec: specs[\pitch],
				numslot: 2
			),
			(
				uname: (osc++"_wt_pos").asSymbol,
				name: "Wt-pos",
				kind: \knob,
				spec: specs[\wt_pos],
				numslot: 3
			),
			(
				uname: (osc++"_intensity").asSymbol,
				name: "Intensity",
				kind: \knob,
				spec: \freq.asSpec,
				numslot: 3
			),
			(
				uname: (osc++"_amp").asSymbol,
				name: "Amp",
				kind: \knob,
				spec: specs[\amp],
				val: 0.5,
				numslot: 2
			),
			(
				uname: osc,
				name: "Osc"++idx,
				kind: \mute
			),
			(
				uname: (osc++"_wt").asSymbol,
				name: "Wt",
				indexes: idx,
				kind: \wavetable
			),
			(
				uname: (osc++"_fader").asSymbol,
				name: "Filter output",
				numslot: 0,
				spec: \unipolar.asSpec,
				kind: \knob
			),
		];
	};

	///////// modosc params
	params[\modosc] = [
		(
			uname: \modosc_pitch,
			name: "Pitch",
			kind: \knob,
			spec: \widefreq.asSpec,
			numslot: 2
		),
		(
			uname: \modosc_ring,
			name: "Amp",
			kind: \knob,
			spec: specs[\amp],
			numslot: 2
		),
		(
			uname: \modosc_phase,
			name: "Phase",
			kind: \knob,
			spec: \freq.asSpec,
			numslot: 2
		),
		(
			uname: \modosc_position,
			name: "Position",
			kind: \knob,
			spec: specs[\wt_pos],
			numslot: 2
		),
		(
			uname: \modosc_filterfm,
			name: "Filter FM",
			kind: \knob,
			spec: \freq.asSpec,
			numslot: 2
		),
		(
			uname: \modosc_matrix,
			name: "Mod routing matrix",
			kind: \matrix
		),
		(
			uname: \modosc,
			name: "Modulation Osc",
			kind: \mute
		),
	];

	///////// noise params
	params[\noise] = [
		(
			uname: \noise_color,
			name: "Color",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		),
		(
			uname: \noise_amp,
			name: "Amp",
			kind: \knob,
			spec: specs[\amp],
			numslot: 2
		),
		(
			uname: \noise,
			name: "Noise",
			kind: \mute
		),
		(
			uname: \noise_kind,
			name: "Noise",
			bank: \noise,
			kind: \kind
		),
		(
			uname: \noise_fader,
			name: "Noise fader",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 0
		)
	];

	///////// feedback params
	params[\feedback] = [
		(
			uname: \feedback_amp,
			name: "Amp",
			kind: \knob,
			spec: specs[\wideamp],
			numslot: 2
		),
		(
			uname: \feedback,
			name: "Feedback",
			kind: \mute
		),
		(
			uname: \feedback_fader,
			name: "Feedback fader",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 0
		)
	];

	//////// filter params
	2.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("filter"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_arg1").asSymbol,
				name: "Arg1",
				arg_index: 0,
				kind: \multiknob,
				spec: \freq.asSpec,
				numslot: 3
			),
			(
				uname: (osc++"_arg2").asSymbol,
				name: "Arg2",
				arg_index: 1,
				kind: \multiknob,
				spec: \rq.asSpec,
				numslot: 3
			),
			(
				uname: (osc++"_arg3").asSymbol,
				name: "Arg3",
				arg_index: 2,
				kind: \multiknob,
				spec: \rq.asSpec,
				numslot: 2
			),
			(
				uname: osc,
				name: "Filter "++idx,
				kind: \mute
			),
			(
				uname: (osc++"_kind").asSymbol,
				name: "filter kind",
				bank: \filter,
				knobs: 3.collect { arg i; (osc++"_arg"++(i+1)).asSymbol },
				kind: \kind
			),
			(
				uname: (osc++"_amp").asSymbol,
				name: "Filter Amp",
				kind: \knob,
				spec: specs[\amp],
				numslot: 0
			)
		];
	};

	//////// others params
	params[\others] = [
		(
			uname: \filter_parseq,
			name: "Par Seq",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		),
		(
			uname: \filter_mix,
			name: "Filter Mix",
			kind: \knob,
			spec: \unipolar.asSpec,
			numslot: 2
		)
	];


	//////// master pan params
	params[\master_pan] = [
		(
			uname: \amp_mod,
			name: "Amp Mod",
			kind: \knob,
			spec: specs[\amp],
			numslot: 2
		),
		(
			uname: \pan,
			name: "Pan",
			kind: \knob,
			spec: \pan.asSpec,
			numslot: 2
		),
		(
			uname: \master_pan,
			name: "Amp",
			kind: \mute
		)
	];

	//////// bypass params
	params[\bypass] = [
		(
			uname: \bypass_mod,
			name: "Byp Mod",
			kind: \mod_slot,
			numslot: 2
		),
		(
			uname: \bypass,
			name: "Bypass",
			kind: \mute
		),
		(
			uname: \bypass_amp,
			name: "Bypass Amp",
			spec: specs[\amp],
			kind: \knob,
			numslot: 0
		)
	];

	//////// master params
	params[\master] = [
		(
			uname: \amp,
			name: "Master Volume",
			kind: \knob,
			spec: specs[\wideamp],
			numslot: 0
		),
		(
			// fake muter: can't mute master
			uname: \master,
			name: "Master Volume",
			kind: \mute
		),
	];

	//////// FX params
	2.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("fx"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_arg1").asSymbol,
				name: "Arg1",
				arg_index: 0,
				kind: \multiknob,
				spec: \unipolar.asSpec,
				numslot: 2
			),
			(
				uname: (osc++"_arg2").asSymbol,
				name: "Arg2",
				arg_index: 1,
				kind: \multiknob,
				numslot: 2
			),
			(
				uname: (osc++"_arg3").asSymbol,
				name: "Arg3",
				arg_index: 2,
				kind: \multiknob,
				numslot: 2
			),
			(
				uname: (osc++"_arg4").asSymbol,
				name: "Arg4",
				arg_index: 3,
				kind: \multiknob,
				numslot: 2
			),
			(
				uname: osc,
				name: "FX"++idx,
				kind: \mute
			),
			(
				uname: (osc++"_kind").asSymbol,
				name: "fx kind",
				bank: \fx,
				knobs: 4.collect { arg idx; (osc++"_arg"++(idx+1)).asSymbol },
				kind: \kind
			)
		];
	};

	
	//////// Eq params
	params[\eq] = [
			(
				uname: \eq_lowshelf,
				name: "Low Shelf",
				arg_index: 0,
				kind: \multiknob,
				spec: \db.asSpec,
				numslot: 2
			),
			(
				uname: \eq_boost,
				name: "Boost",
				arg_index: 1,
				kind: \multiknob,
				spec: \db.asSpec,
				numslot: 2
			),
			(
				uname: \eq_freq,
				name: "Frequency",
				arg_index: 2,
				kind: \multiknob,
				spec: \freq.asSpec,
				numslot: 2
			),
			(
				uname: \eq_highshelf,
				name: "High Shelf",
				arg_index: 3,
				kind: \multiknob,
				spec: \db.asSpec,
				numslot: 2
			),
			(
				uname: \eq,
				name: "Eq",
				kind: \mute
			)

	];

	//////// Insert effects params
	2.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("insert"++idx).asSymbol;
		params[osc] = [
			(
				uname: (osc++"_arg1").asSymbol,
				name: "Arg1",
				kind: \multiknob,
				arg_index: 0,
				spec: \unipolar.asSpec,
				numslot: 2
			),
			(
				uname: (osc++"_arg2").asSymbol,
				name: "Arg2",
				kind: \multiknob,
				arg_index: 1,
				numslot: 2
			),
			(
				uname: osc,
				name: "Insert "++idx,
				kind: \mute
			),
			(
				uname: (osc++"_kind").asSymbol,
				name: "insert kind",
				bank: \insert,
				knobs: 2.collect { arg idx; (osc++"_arg"++(idx+1)).asSymbol },
				kind: \kind
			)
		];
	};

	//////// Env params

	4.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("env"++idx).asSymbol;

		params[osc] = [
			(
				uname: (osc++"_vel").asSymbol,
				name: "Vel",
				kind: \knob,
				spec: specs[\velocity],
				numslot: 0
			),
			(
				uname: (osc++"_ktr").asSymbol,
				name: "KTR",
				kind: \knob,
				spec: specs[\ktr],
				numslot: 0
			),
			(
				uname: (osc++"_delay_time").asSymbol,
				name: "Delay",
				kind: \knob,
				spec: \delay.asSpec,
				val: 0,
				numslot: 1
			),
			(
				uname: (osc++"_attack_time").asSymbol,
				name: "Attack Time",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			),
			(
				uname: (osc++"_attack_level").asSymbol,
				name: "Attack level",
				kind: \knob,
				spec: specs[\envamp],
				numslot: 1
			),
			(
				uname: (osc++"_decay_time").asSymbol,
				name: "Decay time",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			),
			(
				uname: (osc++"_decay_level").asSymbol,
				name: "Decay level",
				kind: \knob,
				spec: specs[\envamp],
				val: 0.8,
				numslot: 1
			),
			(
				uname: (osc++"_sustain_time").asSymbol,
				name: "Sustain loop",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			),
			(
				uname: (osc++"_sustain_level").asSymbol,
				name: "Sustain level",
				kind: \knob,
				spec: specs[\envamp],
				numslot: 1
			),
			(
				uname: (osc++"_release_time").asSymbol,
				name: "Release",
				kind: \knob,
				spec: specs[\env],
				numslot: 1
			)
		]
	};

	//////// Modulator params (lfo, stepper, performer)

	4.do { arg idx;
		var osc;
		idx = idx+1;
		osc = ("modulator"++idx).asSymbol;

		params[osc] = [
			(
				uname: (osc++"_kind").asSymbol,
				name: "kind",
				kind: \kind,
				bank: \modulator
			),
			(
				uname: (osc++"_rate").asSymbol,
				name: "Rate",
				kind: \knob,
				spec: specs[\rate],
				numslot: 1
			),
			(
				uname: (osc++"_amp").asSymbol,
				name: "Amp",
				kind: \knob,
				spec: specs[\amp],
				numslot: 1
			),
			(
				uname: (osc++"_ampmod").asSymbol,
				name: "Amp Mod",
				kind: \knob,
				spec: specs[\amp],
				numslot: 1
			),
			(
				uname: (osc++"_glidefade").asSymbol,
				name: "Glide/Fade",
				kind: \knob,
				spec: specs[\glidefade],
				numslot: 1
			),
			(
				uname: (osc++"_steps1").asSymbol,
				name: "Steps1",
				indexes: [idx-1, \ampmod],
				kind: \steps
			),
			(
				uname: (osc++"_steps2").asSymbol,
				name: "Steps2",
				indexes: [idx-1, \glidefade],
				kind: \steps
			),
			(
				uname: (osc++"_steps_amp").asSymbol,
				name: "Steps amp",
				indexes: [idx-1, \amp],
				kind: \steps
			),
			(
				uname: (osc++"_curve1").asSymbol,
				name: "Curve1",
				indexes: [idx-1, 0],
				kind: \curve
			),
			(
				uname: (osc++"_curve2").asSymbol,
				name: "Curve2",
				indexes: [idx-1, 1],
				kind: \curve
			),
			(
				uname: (osc++"_perfcurve1").asSymbol,
				name: "Curve1",
				indexes: [idx-1, 0],
				kind: \perfcurve
			),
			(
				uname: (osc++"_perfcurve2").asSymbol,
				name: "Curve2",
				indexes: [idx-1, 1],
				kind: \perfcurve
			),
			(
				uname: (osc++"_env_attack").asSymbol,
				name: "Internal Env Attack",
				kind: \knob,
				spec: specs[\env],
				numslot: 0
			),
			(
				uname: (osc++"_env_decay").asSymbol,
				name: "Internal Env Decay",
				kind: \knob,
				spec: specs[\env],
				numslot: 0
			),
		];
	};

	//////// Macro params

	params[\macro] = 8.collect { arg idx;
		var osc;
		idx = idx+1;
		osc = ("macro"++idx).asSymbol;

		(
			uname: (osc++"_control").asSymbol,
			name: "Macro "++idx,
			arg_index: idx-1,
			kind: \macro,
			val: 0,
			slotnum: 0,
		)
	};

	//////// Routing

	params[\routing] = {

		var names = [\insert1, \insert2, \feedback, \bypass_osc, \bypass_dest];
		var labels = ["Ins1", "Ins2", "Feedback", "Bypass source", "Bypass destination"];
		names.collect { arg name, idx;
			(
				uname: ("routing_"++name).asSymbol,
				name: labels[idx],
				bank: ("routing_"++name.asString.replace("1", "").replace("2", "")).asSymbol,
				routing_name: name,
				kind: \kind,
				transmit: \routing
			)
		}

	}.value;

	//////// Voicing

	//voicing unisono
	//voicing pitch_lorange
	//voicing pitch_hirange
	//voicing pitch_chord
	//voicing wavetable_lorange
	//voicing wavetable_hirange

	//voicing enable_pan_spreading
	//enabled enable_pitch_spreading
	//enabled wavetable_spreading

	//pitch_spread
	//wavetable_spread
	//pan_spread

	params[\voicing] = [
			(
				uname: \voicing_unisono,
				kind: \knob,
				numslot: 0,
				spec: ControlSpec.new(1,64,\lin,1,1),
				transmit: \voicing
			),
			(
				uname: \voicing_pitch_lorange,
				kind: \knob,
				numslot: 0,
				spec: specs[\pitch],
				transmit: \voicing
			),
			(
				uname: \voicing_pitch_hirange,
				kind: \knob,
				numslot: 0,
				spec: specs[\pitch],
				transmit: \voicing
			),
			(
				uname: \voicing_wavetable_lorange,
				kind: \knob,
				numslot: 0,
				spec: \unipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_wavetable_hirange,
				kind: \knob,
				numslot: 0,
				spec: \unipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_pan_lorange,
				kind: \knob,
				numslot: 0,
				spec: \unipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_pan_hirange,
				kind: \knob,
				numslot: 0,
				spec: \unipolar.asSpec,
				transmit: \voicing
			),
			(
				uname: \voicing_enable_pitch,
				kind: \mute,
				val: 1,
				transmit: \voicing
			),
			(
				uname: \voicing_enable_wavetable,
				kind: \mute,
				val: 1,
				transmit: \voicing
			),
			(
				uname: \voicing_enable_pan,
				kind: \mute,
				val: 1,
				transmit: \voicing
			),

			(
				uname: \pitch_spread,
				name: "Pitch cutoff",
				kind: \knob,
				val: 0,
				spec: specs[\pitch],
				numslot: 1,
			),
			(
				uname: \wavetable_spread,
				name: "Wavetable position",
				kind: \knob,
				val: 0,
				spec: \unipolar.asSpec,
				numslot: 1,
			),
			(
				uname: \pan_spread,
				name: "Pan",
				kind: \knob,
				val: 0,
				spec: \unipolar.asSpec,
				numslot: 1,
			),
	];


	////////// presets

	params[\presets] = [
			(
				uname: \presets_global,
				name: "Global presets",
				kind: \preset,
			)
	];


	params;

};
