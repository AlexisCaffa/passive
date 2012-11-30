
Instr(\passive, { arg 

		////// NonControl data,

		enabled,
		kinds,
		mod,
		routing,
		steps,

		/////// busses

		modulation_bus=#[0,0,0,0, 0,0,0,0],

		////// master,

		freq=200,
		amp=0.1,
		pan=0,
		spread=1,
		amp_mod,
		gate=1,

		//////// ktr

		ktr_osc1_freq,
		ktr_osc2_freq,
		ktr_osc3_freq,
		ktr_mosc_freq,
		ktr_insfx_freq,

		ktr_filter1_freq,
		ktr_filter2_freq,

		///////// voincing

		pitch_spread,
		wavetable_spread,
		pan_spread,

		/////// oscs,

		osc1_amp,
		osc1_fader,
		osc1_intensity,
		osc1_pitch,
		osc1_wt,
		osc1_wt_pos,
		osc2_amp,
		osc2_fader,
		osc2_intensity,
		osc2_pitch,
		osc2_wt,
		osc2_wt_pos,
		osc3_amp,
		osc3_fader,
		osc3_intensity,
		osc3_pitch,
		osc3_wt,
		osc3_wt_pos,

		modosc_filterfm,
		modosc_phase,
		modosc_pitch,
		modosc_position,
		modosc_ring,

		noise_amp,
		noise_color,
		noise_fader,

		bypass_amp,
		bypass_mod,

		feedback_amp,
		feedback_fader,

		////// filters,

		filter1_amp,
		filter1_arg1,
		filter1_arg2,
		filter1_arg3,
		filter2_amp,
		filter2_arg1,
		filter2_arg2,
		filter2_arg3,
		filter_mix,
		filter_parseq,

		/////// insert fx,

		insert1_arg1,
		insert1_arg2,
		insert2_arg1,
		insert2_arg2,

		/////// fx,
		
		fx1_arg1,
		fx1_arg2,
		fx1_arg3,
		fx1_arg4,
		fx2_arg1,
		fx2_arg2,
		fx2_arg3,
		fx2_arg4,

		eq_boost,
		eq_freq,
		eq_highshelf,
		eq_lowshelf,

		////// env,

		env1_attack_level,
		env1_attack_time,
		env1_decay_level,
		env1_decay_time,
		env1_delay_time,
		env1_ktr,
		env1_release_time,
		env1_sustain_level,
		env1_sustain_time,
		env1_vel,
		env2_attack_level,
		env2_attack_time,
		env2_decay_level,
		env2_decay_time,
		env2_delay_time,
		env2_ktr,
		env2_release_time,
		env2_sustain_level,
		env2_sustain_time,
		env2_vel,
		env3_attack_level,
		env3_attack_time,
		env3_decay_level,
		env3_decay_time,
		env3_delay_time,
		env3_ktr,
		env3_release_time,
		env3_sustain_level,
		env3_sustain_time,
		env3_vel,
		env4_attack_level,
		env4_attack_time,
		env4_decay_level,
		env4_decay_time,
		env4_delay_time,
		env4_ktr,
		env4_release_time,
		env4_sustain_level,
		env4_sustain_time,
		env4_vel,

		///// modulators,

		modulator1_amp,
		modulator1_ampmod,
		modulator1_curve1,
		modulator1_curve2,
		modulator1_env_attack,
		modulator1_env_decay,
		modulator1_glidefade,
		modulator1_rate,
		modulator2_amp,
		modulator2_ampmod,
		modulator2_curve1,
		modulator2_curve2,
		modulator2_env_attack,
		modulator2_env_decay,
		modulator2_glidefade,
		modulator2_rate,
		modulator3_amp,
		modulator3_ampmod,
		modulator3_curve1,
		modulator3_curve2,
		modulator3_env_attack,
		modulator3_env_decay,
		modulator3_glidefade,
		modulator3_rate,
		modulator4_amp,
		modulator4_ampmod,
		modulator4_curve1,
		modulator4_curve2,
		modulator4_env_attack,
		modulator4_env_decay,
		modulator4_glidefade,
		modulator4_rate
	;

	var ou, oudb;
	var freq1, freq2, freq3;
	var noise, modosc;
	var osc1, osc1_f1, osc1_f2;
	var osc2, osc2_f1, osc2_f2;
	var osc3, osc3_f1, osc3_f2;
	var f1, f2, f1_2;
	var feedback_f1, feedback_f2;
	var noise_f1, noise_f2;

	var modulators_bus;
	var modulator1_phase = 0, modulator2_phase = 0, modulator3_phase = 0, modulator4_phase = 0;

	var input, input_mod, modulate, modulators;
	var feedback, insert_effect, insert_feedback, onoff, bypass;
	var bypass_osc, bypass_dest, bypass_signal = 0;
	var build_spread_array, build_freq_spread_array;

	//var kinds;

	debug("------ passive: BEGIN");
	osc1_wt.debug("------ passive: osc1_wt");
	//enabled[\noise].debug("------ passive: enable noise");

	//////////////////

	build_spread_array = { arg unisono;
		var z, ret;
		if(unisono.asInteger.odd) {
			z = (unisono-1 / 2).asInteger;
			ret = z.collect { arg i; (i+1)/z };
			ret = 0-ret.reverse ++ 0 ++ ret;
		} {
			z = (unisono / 2).asInteger;
			ret = z.collect { arg i; (i+1)/z };
			ret = 0-ret.reverse ++ ret;
		};
	};

	modulate = { arg argname, inval, modarray;
		// TODO: cliping
		var sig, sigmod, range;
		sig = inval;
		if(mod[argname].notNil and:{mod[argname][\spec].notNil}) {
			var normsig;
			sigmod = 0;
			normsig = mod[argname][\spec].unmap(sig);
			3.do { arg index;
				if(mod[argname][index].notNil and: { modarray[ mod[argname][index][\source] ].notNil }) {
					[argname, index].debug("modulate");
					mod[argname][index].debug("mod");
					range = mod[argname][index][\norm_range] ?? 0;
					range.debug("range");
					sigmod = sigmod + (modarray[ mod[argname][index][\source] ] * range)
				};
			};
			sig = mod[argname][\spec].map(normsig + (sigmod));
		};
		sig;
	};

	onoff = { arg in, kind;
		if(enabled[kind]) {
			in;
		} {
			0;
		}
	};

	bypass = { arg in, kind, fun;
		if(enabled[kind]) {
			fun.value;
		} {
			in;
		}
	};

	bypass_osc = { arg in, key;
		if(routing[\bypass_osc] == key) {
			bypass_signal = in;
			0;
		} {
			in;
		};
	};

	insert_effect = { arg in, pos;
		if(routing[\insert1] == pos and:{ enabled[\insert1] }) {
			Instr(\p_ins_effect).value((kind: kinds[\insert1], in:in, arg1:insert1_arg1, arg2:insert1_arg2));
		} {
			if(routing[\insert2] == pos and:{ enabled[\insert2] }) {
				Instr(\p_ins_effect).value((kind: kinds[\insert2], in:in, arg1:insert2_arg1, arg2:insert2_arg2));
			} {
				in;
			}
		};
	};

	insert_feedback = { arg in, pos;
		if(routing[\feedback] == pos) {
			in = in.sum;
			in = insert_effect.(in, \in_feedback);
			LocalOut.ar(in);
		}
	};

	modulation_bus.debug("modulation_bus");
	routing[\modulation_fxbus].debug("modulation_fxbus");

	modulators_bus = 8.collect { arg idx;
		InFeedback.ar(modulation_bus[idx], 1);
	};

	input_mod = {
		var keys;
		var ret;
		var input_array;
		var vals;
		keys = #[

			////// env,

			env1_attack_level,
			env1_attack_time,
			env1_decay_level,
			env1_decay_time,
			env1_delay_time,
			env1_ktr,
			env1_release_time,
			env1_sustain_level,
			env1_sustain_time,
			env1_vel,
			env2_attack_level,
			env2_attack_time,
			env2_decay_level,
			env2_decay_time,
			env2_delay_time,
			env2_ktr,
			env2_release_time,
			env2_sustain_level,
			env2_sustain_time,
			env2_vel,
			env3_attack_level,
			env3_attack_time,
			env3_decay_level,
			env3_decay_time,
			env3_delay_time,
			env3_ktr,
			env3_release_time,
			env3_sustain_level,
			env3_sustain_time,
			env3_vel,
			env4_attack_level,
			env4_attack_time,
			env4_decay_level,
			env4_decay_time,
			env4_delay_time,
			env4_ktr,
			env4_release_time,
			env4_sustain_level,
			env4_sustain_time,
			env4_vel,

			///// modulators,

			modulator1_amp,
			modulator1_ampmod,
			modulator1_curve1,
			modulator1_curve2,
			modulator1_env_attack,
			modulator1_env_decay,
			modulator1_glidefade,
			modulator1_rate,
			modulator2_amp,
			modulator2_ampmod,
			modulator2_curve1,
			modulator2_curve2,
			modulator2_env_attack,
			modulator2_env_decay,
			modulator2_glidefade,
			modulator2_rate,
			modulator3_amp,
			modulator3_ampmod,
			modulator3_curve1,
			modulator3_curve2,
			modulator3_env_attack,
			modulator3_env_decay,
			modulator3_glidefade,
			modulator3_rate,
			modulator4_amp,
			modulator4_ampmod,
			modulator4_curve1,
			modulator4_curve2,
			modulator4_env_attack,
			modulator4_env_decay,
			modulator4_glidefade,
			modulator4_rate
		];
		vals = [
			////// env,

			env1_attack_level,
			env1_attack_time,
			env1_decay_level,
			env1_decay_time,
			env1_delay_time,
			env1_ktr,
			env1_release_time,
			env1_sustain_level,
			env1_sustain_time,
			env1_vel,
			env2_attack_level,
			env2_attack_time,
			env2_decay_level,
			env2_decay_time,
			env2_delay_time,
			env2_ktr,
			env2_release_time,
			env2_sustain_level,
			env2_sustain_time,
			env2_vel,
			env3_attack_level,
			env3_attack_time,
			env3_decay_level,
			env3_decay_time,
			env3_delay_time,
			env3_ktr,
			env3_release_time,
			env3_sustain_level,
			env3_sustain_time,
			env3_vel,
			env4_attack_level,
			env4_attack_time,
			env4_decay_level,
			env4_decay_time,
			env4_delay_time,
			env4_ktr,
			env4_release_time,
			env4_sustain_level,
			env4_sustain_time,
			env4_vel,

			///// modulators,

			modulator1_amp,
			modulator1_ampmod,
			modulator1_curve1,
			modulator1_curve2,
			modulator1_env_attack,
			modulator1_env_decay,
			modulator1_glidefade,
			modulator1_rate,
			modulator2_amp,
			modulator2_ampmod,
			modulator2_curve1,
			modulator2_curve2,
			modulator2_env_attack,
			modulator2_env_decay,
			modulator2_glidefade,
			modulator2_rate,
			modulator3_amp,
			modulator3_ampmod,
			modulator3_curve1,
			modulator3_curve2,
			modulator3_env_attack,
			modulator3_env_decay,
			modulator3_glidefade,
			modulator3_rate,
			modulator4_amp,
			modulator4_ampmod,
			modulator4_curve1,
			modulator4_curve2,
			modulator4_env_attack,
			modulator4_env_decay,
			modulator4_glidefade,
			modulator4_rate
		];
		ret = Dictionary.new;
		input_array = List.new;
		keys.do { arg key, i;
			var val;
			val = modulate.(key, vals[i], modulators_bus);
			ret[key] = val;
			input_array.add(val);
			//[keys, vals[i], val].debug("passive: input: keys, valsi, val");
		};
		//mod.do { arg key, i;
		//	var val;
		//	var idx = input_dict[key];
		//	val = modulate.(key, vals[idx]);
		//	ret[key] = val;
		//	input_array.add(val);
		//	[keys, vals[i], val].debug("passive: input: keys, valsi, val");
		//};
		//input_array.debug("passive: input: input_array");
		# 

			////// env,

			env1_attack_level,
			env1_attack_time,
			env1_decay_level,
			env1_decay_time,
			env1_delay_time,
			env1_ktr,
			env1_release_time,
			env1_sustain_level,
			env1_sustain_time,
			env1_vel,
			env2_attack_level,
			env2_attack_time,
			env2_decay_level,
			env2_decay_time,
			env2_delay_time,
			env2_ktr,
			env2_release_time,
			env2_sustain_level,
			env2_sustain_time,
			env2_vel,
			env3_attack_level,
			env3_attack_time,
			env3_decay_level,
			env3_decay_time,
			env3_delay_time,
			env3_ktr,
			env3_release_time,
			env3_sustain_level,
			env3_sustain_time,
			env3_vel,
			env4_attack_level,
			env4_attack_time,
			env4_decay_level,
			env4_decay_time,
			env4_delay_time,
			env4_ktr,
			env4_release_time,
			env4_sustain_level,
			env4_sustain_time,
			env4_vel,

			///// modulators,

			modulator1_amp,
			modulator1_ampmod,
			modulator1_curve1,
			modulator1_curve2,
			modulator1_env_attack,
			modulator1_env_decay,
			modulator1_glidefade,
			modulator1_rate,
			modulator2_amp,
			modulator2_ampmod,
			modulator2_curve1,
			modulator2_curve2,
			modulator2_env_attack,
			modulator2_env_decay,
			modulator2_glidefade,
			modulator2_rate,
			modulator3_amp,
			modulator3_ampmod,
			modulator3_curve1,
			modulator3_curve2,
			modulator3_env_attack,
			modulator3_env_decay,
			modulator3_glidefade,
			modulator3_rate,
			modulator4_amp,
			modulator4_ampmod,
			modulator4_curve1,
			modulator4_curve2,
			modulator4_env_attack,
			modulator4_env_decay,
			modulator4_glidefade,
			modulator4_rate
			= input_array;
		ret;
	}.value;

	modulators = [
		Instr(\p_env).value((
			gate:gate, delayTime:env1_delay_time, attackTime:env1_attack_time, decayTime:env1_decay_time,
			sustainLevel:env1_decay_level, releaseTime:env1_release_time, peakLevel:env1_attack_level, curve:0
		)),
		Instr(\p_env).value((
			gate:gate, delayTime:env2_delay_time, attackTime:env2_attack_time, decayTime:env2_decay_time,
			sustainLevel:env2_decay_level, releaseTime:env2_release_time, peakLevel:env2_attack_level, curve:0
		)),
		Instr(\p_env).value((
			gate:gate, delayTime:env3_delay_time, attackTime:env3_attack_time, decayTime:env3_decay_time,
			sustainLevel:env3_decay_level, releaseTime:env3_release_time, peakLevel:env3_attack_level, curve:0
		)),
		Instr(\p_env).value((
			gate:gate, delayTime:env4_delay_time, attackTime:env4_attack_time, decayTime:env4_decay_time,
			sustainLevel:env4_decay_level, releaseTime:env4_release_time, peakLevel:env4_attack_level, curve:0
		)),

		Instr(\p_modulator).value((
			kind:kinds[\modulator1], internal_mod:mod[\internal_mod1], steps:steps[0], gate:gate, amp:modulator1_amp, rate:modulator1_rate, 
			attackTime:modulator1_env_attack, releaseTime:modulator1_env_decay, phase:modulator1_phase,
			amp_mod:modulator1_ampmod, glidefade_mod:modulator1_glidefade, curve1:modulator1_curve1, curve2:modulator1_curve2
		)),
		Instr(\p_modulator).value((
			kind:kinds[\modulator2], internal_mod:mod[\internal_mod2], steps:steps[1], gate:gate, amp:modulator2_amp, rate:modulator2_rate, 
			attackTime:modulator2_env_attack, releaseTime:modulator2_env_decay, phase:modulator2_phase,
			amp_mod:modulator2_ampmod, glidefade_mod:modulator2_glidefade, curve1:modulator2_curve1, curve2:modulator2_curve2
		)),
		Instr(\p_modulator).value((
			kind:kinds[\modulator3], internal_mod:mod[\internal_mod3], steps:steps[2], gate:gate, amp:modulator3_amp, rate:modulator3_rate, 
			attackTime:modulator3_env_attack, releaseTime:modulator3_env_decay, phase:modulator3_phase,
			amp_mod:modulator3_ampmod, glidefade_mod:modulator3_glidefade, curve1:modulator3_curve1, curve2:modulator3_curve2
		)),
		Instr(\p_modulator).value((
			kind:kinds[\modulator4], internal_mod:mod[\internal_mod4], steps:steps[3], gate:gate, amp:modulator4_amp, rate:modulator4_rate, 
			attackTime:modulator4_env_attack, releaseTime:modulator4_env_decay, phase:modulator4_phase,
			amp_mod:modulator4_ampmod, glidefade_mod:modulator4_glidefade, curve1:modulator4_curve1, curve2:modulator4_curve2
		))
	];

	modulators.do { arg modulator, idx;
		
		Out.ar(modulation_bus[idx], modulator);
		ReplaceOut.ar(routing[\modulation_fxbus][idx], modulator);
	};

	//input = {
	//	var keys;
	//	var ret;
	//	var input_array;
	//	var vals;
	//	keys = #[
	//		amp, gate, pan, spread, freq,
	//		osc1_amp, osc1_wt, osc1_fader,
	//		osc2_amp, osc2_wt, osc2_fader,
	//		osc3_amp, osc3_wt, osc3_fader,
	//		filter1_arg1, filter1_arg2, filter1_arg3,
	//		filter2_arg1, filter2_arg2, filter2_arg3,
	//		filter_mix, filter_parseq
	//	];
	//	vals = [
	//		amp, gate, pan, spread, freq,
	//		osc1_amp, osc1_wt, osc1_fader,
	//		osc2_amp, osc2_wt, osc2_fader,
	//		osc3_amp, osc3_wt, osc3_fader,
	//		filter1_arg1, filter1_arg2, filter1_arg3,
	//		filter2_arg1, filter2_arg2, filter2_arg3,
	//		filter_mix, filter_parseq
	//	];
	//	ret = Dictionary.new;
	//	input_array = List.new;
	//	keys.do { arg key, i;
	//		var val;
	//		val = modulate.(key, vals[i]);
	//		input_array.add(ret[key]);
	//	};
	//	# amp, gate, pan, spread, freq,
	//		osc1_amp, osc1_wt, osc1_fader,
	//		osc2_amp, osc2_wt, osc2_fader,
	//		osc3_amp, osc3_wt, osc3_fader,
	//		filter1_arg1, filter1_arg2, filter1_arg3,
	//		filter2_arg1, filter2_arg2, filter2_arg3,
	//		filter_mix, filter_parseq 
	//		= input_array;
	//	ret;
	//}.value;

	input = {
		var keys;
		var ret;
		var input_array;
		var vals;
		keys = #[
			pan,
			///////// voincing

			pitch_spread,
			wavetable_spread,
			pan_spread,

			/////// oscs,

			osc1_amp,
			osc1_fader,
			osc1_intensity,
			osc1_pitch,
			osc1_wt,
			osc1_wt_pos,
			osc2_amp,
			osc2_fader,
			osc2_intensity,
			osc2_pitch,
			osc2_wt,
			osc2_wt_pos,
			osc3_amp,
			osc3_fader,
			osc3_intensity,
			osc3_pitch,
			osc3_wt,
			osc3_wt_pos,

			modosc_filterfm,
			modosc_phase,
			modosc_pitch,
			modosc_position,
			modosc_ring,

			noise_amp,
			noise_color,
			noise_fader,

			bypass_amp,
			bypass_mod,

			feedback_amp,
			feedback_fader,

			////// filters,

			filter1_amp,
			filter1_arg1,
			filter1_arg2,
			filter1_arg3,
			filter2_amp,
			filter2_arg1,
			filter2_arg2,
			filter2_arg3,
			filter_mix,
			filter_parseq,

			/////// insert fx,

			insert1_arg1,
			insert1_arg2,
			insert2_arg1,
			insert2_arg2,

			/////// fx,
			
		//	fx1_arg1,
		//	fx1_arg2,
		//	fx1_arg3,
		//	fx1_arg4,
		//	fx2_arg1,
		//	fx2_arg2,
		//	fx2_arg3,
		//	fx2_arg4,

		//	eq_boost,
		//	eq_freq,
		//	eq_highshelf,
		//	eq_lowshelf,
		];
		vals = [
			pan,
			///////// voincing

			pitch_spread,
			wavetable_spread,
			pan_spread,

			/////// oscs,

			osc1_amp,
			osc1_fader,
			osc1_intensity,
			osc1_pitch,
			osc1_wt,
			osc1_wt_pos,
			osc2_amp,
			osc2_fader,
			osc2_intensity,
			osc2_pitch,
			osc2_wt,
			osc2_wt_pos,
			osc3_amp,
			osc3_fader,
			osc3_intensity,
			osc3_pitch,
			osc3_wt,
			osc3_wt_pos,

			modosc_filterfm,
			modosc_phase,
			modosc_pitch,
			modosc_position,
			modosc_ring,

			noise_amp,
			noise_color,
			noise_fader,

			bypass_amp,
			bypass_mod,

			feedback_amp,
			feedback_fader,

			////// filters,

			filter1_amp,
			filter1_arg1,
			filter1_arg2,
			filter1_arg3,
			filter2_amp,
			filter2_arg1,
			filter2_arg2,
			filter2_arg3,
			filter_mix,
			filter_parseq,

			/////// insert fx,

			insert1_arg1,
			insert1_arg2,
			insert2_arg1,
			insert2_arg2,

			/////// fx,
			
		//	fx1_arg1,
		//	fx1_arg2,
		//	fx1_arg3,
		//	fx1_arg4,
		//	fx2_arg1,
		//	fx2_arg2,
		//	fx2_arg3,
		//	fx2_arg4,

		//	eq_boost,
		//	eq_freq,
		//	eq_highshelf,
		//	eq_lowshelf,
		];
		ret = Dictionary.new;
		input_array = List.new;
		keys.do { arg key, i;
			var val;
			val = modulate.(key, vals[i], modulators);
			ret[key] = val;
			input_array.add(val);
			//[keys, vals[i], val].debug("passive: input: keys, valsi, val");
		};
		//mod.do { arg key, i;
		//	var val;
		//	var idx = input_dict[key];
		//	val = modulate.(key, vals[idx]);
		//	ret[key] = val;
		//	input_array.add(val);
		//	[keys, vals[i], val].debug("passive: input: keys, valsi, val");
		//};
		//input_array.debug("passive: input: input_array");
		# 

			pan,

			///////// voincing

			pitch_spread,
			wavetable_spread,
			pan_spread,

			/////// oscs,

			osc1_amp,
			osc1_fader,
			osc1_intensity,
			osc1_pitch,
			osc1_wt,
			osc1_wt_pos,
			osc2_amp,
			osc2_fader,
			osc2_intensity,
			osc2_pitch,
			osc2_wt,
			osc2_wt_pos,
			osc3_amp,
			osc3_fader,
			osc3_intensity,
			osc3_pitch,
			osc3_wt,
			osc3_wt_pos,

			modosc_filterfm,
			modosc_phase,
			modosc_pitch,
			modosc_position,
			modosc_ring,

			noise_amp,
			noise_color,
			noise_fader,

			bypass_amp,
			bypass_mod,

			feedback_amp,
			feedback_fader,

			////// filters,

			filter1_amp,
			filter1_arg1,
			filter1_arg2,
			filter1_arg3,
			filter2_amp,
			filter2_arg1,
			filter2_arg2,
			filter2_arg3,
			filter_mix,
			filter_parseq,

			/////// insert fx,

			insert1_arg1,
			insert1_arg2,
			insert2_arg1,
			insert2_arg2

			/////// fx,
			
		//	fx1_arg1,
		//	fx1_arg2,
		//	fx1_arg3,
		//	fx1_arg4,
		//	fx2_arg1,
		//	fx2_arg2,
		//	fx2_arg3,
		//	fx2_arg4,

		//	eq_boost,
		//	eq_freq,
		//	eq_highshelf,
		//	eq_lowshelf

			= input_array;
		ret;
	}.value;

	//mod = {
	//	var ret = Dictionary.new;
	//	input.keys.collect { arg key;
	//		ret[key] = [0, nil, 23]
	//	}
	//}.value;

	//kinds = kinds ?? Dictionary.new;
	//kinds[\filter1] = kinds[\filter1] ?? \hpf;
	//kinds[\filter2] = kinds[\filter2] ?? \lpf;

	////////// Modulation Osc
	//debug("------ passive: modulation osc");

	modosc = SinOsc.ar((ktr_mosc_freq.cpsmidi + modosc_pitch).midicps);
	modosc = onoff.(modosc, \modosc);

	///////// Pitch spreading
	routing[\voicing][\unisono].debug("unisono");

	build_freq_spread_array = { arg freq;
		if(routing[\voicing][\enable_pitch]) {
			var array = build_spread_array.(routing[\voicing][\unisono]);
			array.debug("spread array");
			freq = (freq.cpsmidi + (pitch_spread * array)).midicps;
		} {
			freq = freq ! routing[\voicing][\unisono];
		};
		freq;
	};

	if(routing[\voicing][\enable_wavetable]) {
		var array = build_spread_array.(routing[\voicing][\unisono]);
		array.debug("wavetable array");
		osc1_wt_pos = (osc1_wt_pos + (wavetable_spread * kinds[\osc1_wt] * array)).clip(0, kinds[\osc1_wt]);
		osc2_wt_pos = (osc2_wt_pos + (wavetable_spread * kinds[\osc2_wt] * array)).clip(0, kinds[\osc2_wt]);
		osc3_wt_pos = (osc3_wt_pos + (wavetable_spread * kinds[\osc3_wt] * array)).clip(0, kinds[\osc3_wt]);
	} {
		//noop
	};

	freq.debug("llllllllll freq");


	freq1 = build_freq_spread_array.(ktr_osc1_freq);
	freq2 = build_freq_spread_array.(ktr_osc2_freq);
	freq3 = build_freq_spread_array.(ktr_osc3_freq);
	
	//[freq, freq1].debug("------ passive: modulation osc 2");

	switch(routing[\modosc][\phase],
		1, {
			freq1 = (modosc * modosc_phase) + freq1;
		},
		2, {
			freq2 = (modosc * modosc_phase) + freq2;
		},
		3, {
			freq3 = (modosc * modosc_phase) + freq3;
		}
	);

	debug("------ passive: modulation osc 3");

	switch(routing[\modosc][\position],
		1, {
			osc1_wt_pos = (modosc * modosc_position) + osc1_wt_pos;
		},
		2, {
			osc2_wt_pos = (modosc * modosc_position) + osc2_wt_pos;
		},
		3, {
			osc3_wt_pos = (modosc * modosc_position) + osc3_wt_pos;
		}
	);

	debug("------ passive: modulation osc 4");

	switch(routing[\modosc][\filterfm],
		1, {
			filter1_arg1 = (modosc * modosc_filterfm) + filter1_arg1;
		},
		2, {
			filter2_arg1 = (modosc * modosc_filterfm) + filter2_arg1;
		},
	);

	//////// Generators
	debug("------ passive: gen");


	osc1 = Instr(\p_oscillator).value((wt_range: kinds[\osc1_wt], amp:osc1_amp, freq:freq1, detune:osc1_pitch, wt:osc1_wt, wt_position:osc1_wt_pos));
	osc1 = onoff.(osc1, \osc1);
	osc1 = bypass_osc.(osc1, \osc1);

	osc2 = Instr(\p_oscillator).value((wt_range: kinds[\osc2_wt], amp:osc2_amp, freq:freq2, detune:osc2_pitch, wt:osc2_wt, wt_position:osc2_wt_pos));
	osc2 = onoff.(osc2, \osc2);
	osc2 = bypass_osc.(osc2, \osc2);

	osc3 = Instr(\p_oscillator).value((wt_range: kinds[\osc3_wt], amp:osc3_amp, freq:freq3, detune:osc3_pitch, wt:osc3_wt, wt_position:osc3_wt_pos));
	osc3 = onoff.(osc3, \osc3);
	osc3 = bypass_osc.(osc3, \osc3);

	noise = Instr(\p_noise).value((kind:kinds[\noise],color:noise_color, amp:noise_amp));
	noise = onoff.(noise, \noise);

	////// ring mod

	debug("------ passive: gen 5");

	switch(routing[\modosc][\ring],
		1, {
			osc1 = (modosc * modosc_ring) * osc1;
		},
		2, {
			osc2 = (modosc * modosc_ring) * osc2;
		},
		3, {
			osc3 = (modosc * modosc_ring) * osc3;
		}
	);

	///////////////
	debug("------ passive: gen faders");

	feedback = LocalIn.ar(1) * feedback_amp ;
	feedback = feedback.clip(-1,1);
	//Amplitude.kr(feedback).poll;
	//feedback = Limiter.ar(feedback, 1);
	feedback = onoff.(feedback, \feedback);

	osc1_f1 = osc1 * osc1_fader;
	osc1_f2 = osc1 * (1-osc1_fader);

	osc2_f1 = osc2 * osc2_fader;
	osc2_f2 = osc2 * (1-osc2_fader);

	osc3_f1 = osc3 * osc3_fader;
	osc3_f2 = osc3 * (1-osc3_fader);

	noise_f1 = noise * noise_fader;
	noise_f2 = noise * (1-noise_fader);

	feedback_f1 = feedback * feedback_fader;
	feedback_f2 = feedback * (1-feedback_fader);

	//////// Filters
	debug("------ passive: filters");

	f1 = osc1_f1 + osc2_f1 + osc3_f1 + noise_f1 + feedback_f1;
	f1.debug("0 f1");
	f1 = insert_effect.(f1, \before_filter1);
	f1.debug("1 f1");
	f1 = bypass.(f1, \filter1, {
		Instr(\p_filter).value((in:f1, kind:kinds[\filter1], arg1:filter1_arg1, arg2:filter1_arg2, arg3:filter1_arg3, ffreq:ktr_filter1_freq));
	});
	f1.debug("2 f1");
	f1 = insert_effect.(f1, \after_filter1);
	f1.debug("3 f1");
	insert_feedback.(f1, \after_filter1);
	f1.debug("4 f1");

	filter_parseq.debug("------ passive: filters 3");

	f1_2 = insert_effect.(f1, \between_filters);
	f1_2 = f1;
	f1_2 = f1_2 * filter_parseq;

	debug("------ passive: filters 4");


	f2 = osc1_f2 + osc2_f2 + osc3_f2 + noise_f2 + feedback_f2;
	[f1, f2].debug("0 f1, f2");
	f2 = insert_effect.(f2, \before_filter2);
	f2 = f2 + f1_2;
	f2 = bypass.(f2, \filter2, {
		Instr(\p_filter).value((in:f2, kind: kinds[\filter2], arg1:filter2_arg1, arg2:filter2_arg2, arg3:filter2_arg3, ffreq:ktr_filter2_freq));
	});
	f2 = insert_effect.(f2, \after_filter2);

	filter_mix.debug("------ passive: filters 6");

	f1 = f1 * filter1_amp;
	f2 = f2 * filter2_amp;

	insert_feedback.(f2, \after_filter2);

	[f1, f2].debug("f1, f2");
	ou = SelectX.ar(filter_mix, [f2, f1]);

	ou = insert_effect.(ou, \before_pan);

	insert_feedback.(ou, \before_pan);

	//////// Panning - Master Env
	debug("------ passive: pan");

	pan.debug("------ passive: pan");
	spread = 1;
	//ou = SinOsc.ar(200);
	//ou = ou * 10;
	//ou = osc1;
	//ou.poll;

	ou = ou * EnvGen.ar(Env.adsr(0.01,0.1,0.8,0.1),gate,doneAction:2);
	//ou = ou * EnvGen.ar(Env.linen(0.1,0.5,0.5,1),gate,doneAction:2);
	ou.debug("---------- final ou");
	//pan_spread.poll;
	ou = Splay.ar(ou, pan_spread, 1, pan);
	ou.debug("---------- final splay ou");

	//bypass_signal.poll;
	bypass_signal = DC.ar(0);
//	bypass_signal = bypass_signal + DC.ar(0);
//	bypass_signal = bypass_signal * EnvGen.ar(Env.adsr(0.01,0.1,0.8,0.1),gate,doneAction:2);
//	bypass_signal = Splay.ar(bypass_signal!2, spread, 1, pan);
//	bypass_signal = bypass_signal * bypass_amp;
	//ou = Pan2.ar(ou, pan, 1);

	insert_feedback.(ou, \after_pan);

	//////// Effects
	debug("------ passive: fx");

	ou = [ou, bypass_signal];
	//ou;

	//ou = bypass.(ou, \fx1, {
	//	Instr(\p_effect).value((kind:kinds[\fx1], in:ou, arg1:fx1_arg1, arg2:fx1_arg2, arg3:fx1_arg3, arg4:fx1_arg4));
	//});
	//ou = ou + bypass_dest.(\before_fx2);

	//ou = bypass.(ou, \fx2, {
	//	Instr(\p_effect).value((kind:kinds[\fx2], in:ou, arg1:fx2_arg1, arg2:fx2_arg2, arg3:fx2_arg3, arg4:fx2_arg4));
	//});

	//ou = ou + bypass_dest.(\before_eq);

	//ou = bypass.(ou, \eq, {
	//	Instr(\p_effect).value((kind:\eq, in:ou, arg1:eq_lowshelf, arg2:eq_boost, arg3:eq_freq, arg4:eq_highshelf));
	//});
	//oudb = ou;

	//////// Master Volume
//	debug("------ passive: END");
//	ou = oudb;
//	
	ou = ou * amp;
	//ou = ou ! 2;

},[
	NonControlSpec(),
	NonControlSpec(),
	NonControlSpec(),
	NonControlSpec(),
	NonControlSpec(),
]);
//},).addSynthDef;

Instr(\passive_fx, { arg 
		////// NonControl data,
		in,
		in_bypass,

		enabled,
		kinds,
		mod,
		routing,
		steps,

		////// master,

		freq=200,
		amp=0.1,
		pan=0,
		spread=1,
		amp_mod,
		gate=1,

		fx1_arg1,
		fx1_arg2,
		fx1_arg3,
		fx1_arg4,
		fx2_arg1,
		fx2_arg2,
		fx2_arg3,
		fx2_arg4,

		eq_boost,
		eq_freq,
		eq_highshelf,
		eq_lowshelf
	;
	var bypass, ou, modulate, modulators_bus, input;
	var bypass_dest;

	bypass = { arg in, kind, fun;
		if(enabled[kind]) {
			fun.value;
		} {
			in;
		}
	};

	bypass_dest = { arg key;
		if(routing[\bypass_dest] == key) {
			in_bypass;
		} {
			0;
		};
	};

	modulate = { arg argname, inval, modarray;
		// TODO: cliping
		var sig, sigmod, range;
		sig = inval;
		if(mod[argname].notNil and:{mod[argname][\spec].notNil}) {
			var normsig;
			sigmod = 0;
			normsig = mod[argname][\spec].unmap(sig);
			3.do { arg index;
				if(mod[argname][index].notNil and: { modarray[ mod[argname][index][\source] ].notNil }) {
					[argname, index].debug("modulate");
					mod[argname][index].debug("mod");
					range = mod[argname][index][\norm_range] ?? 0;
					range.debug("range");
					sigmod = sigmod + (modarray[ mod[argname][index][\source] ] * range)
				};
			};
			sig = mod[argname][\spec].map(normsig + (sigmod));
		};
		sig;
	};

	debug("passive_fx: BEGIN");

	modulators_bus = 8.collect { arg idx;
		In.ar(routing[\modulation_fxbus][idx], 1);
	};

	input = {
		var keys;
		var ret;
		var input_array;
		var vals;
		keys = #[

			/////// fx,
			
			fx1_arg1,
			fx1_arg2,
			fx1_arg3,
			fx1_arg4,
			fx2_arg1,
			fx2_arg2,
			fx2_arg3,
			fx2_arg4,

			eq_boost,
			eq_freq,
			eq_highshelf,
			eq_lowshelf,
		];
		vals = [
			/////// fx,
			
			fx1_arg1,
			fx1_arg2,
			fx1_arg3,
			fx1_arg4,
			fx2_arg1,
			fx2_arg2,
			fx2_arg3,
			fx2_arg4,

			eq_boost,
			eq_freq,
			eq_highshelf,
			eq_lowshelf,
		];
		ret = Dictionary.new;
		input_array = List.new;
		keys.do { arg key, i;
			var val;
			val = modulate.(key, vals[i], modulators_bus);
			ret[key] = val;
			input_array.add(val);
			//[keys, vals[i], val].debug("passive: input: keys, valsi, val");
		};
		//mod.do { arg key, i;
		//	var val;
		//	var idx = input_dict[key];
		//	val = modulate.(key, vals[idx]);
		//	ret[key] = val;
		//	input_array.add(val);
		//	[keys, vals[i], val].debug("passive: input: keys, valsi, val");
		//};
		//input_array.debug("passive: input: input_array");
		# 
			/////// fx,
			
			fx1_arg1,
			fx1_arg2,
			fx1_arg3,
			fx1_arg4,
			fx2_arg1,
			fx2_arg2,
			fx2_arg3,
			fx2_arg4,

			eq_boost,
			eq_freq,
			eq_highshelf,
			eq_lowshelf

			= input_array;
		ret;
	}.value;

	ou = in;

	ou = ou + bypass_dest.(\before_fx1);

	ou = bypass.(ou, \fx1, {
		Instr(\p_effect).value((kind:kinds[\fx1], in:ou, arg1:fx1_arg1, arg2:fx1_arg2, arg3:fx1_arg3, arg4:fx1_arg4));
	});

	ou = ou + bypass_dest.(\before_fx2);

	ou = bypass.(ou, \fx2, {
		Instr(\p_effect).value((kind:kinds[\fx2], in:ou, arg1:fx2_arg1, arg2:fx2_arg2, arg3:fx2_arg3, arg4:fx2_arg4));
	});

	ou = ou + bypass_dest.(\before_eq);

	//ou = bypass.(ou, \eq, {
	//	Instr(\p_effect).value((kind:\eq, in:ou, arg1:eq_lowshelf, arg2:eq_boost, arg3:eq_freq, arg4:eq_highshelf));
	//});
	//oudb = ou;

	//////// Master Volume
	debug("------ passive: END");
	//ou = oudb;
	
	ou = ou * EnvGate.new(1, gate, 0.5, 2);
	ou = ou * amp;


},[
	\audio,
	\audio,
	NonControlSpec(),
	NonControlSpec(),
	NonControlSpec(),
	NonControlSpec(),
	NonControlSpec(),
]);

Instr(\p_env, { arg gate, delayTime, attackTime, decayTime, sustainLevel, releaseTime, peakLevel, curve;
	EnvGen.ar(Env.dadsr(delayTime, attackTime, decayTime, sustainLevel, releaseTime, peakLevel, curve), gate);
});

Instr(\p_noise, { arg kind, amp=0.1, color=0;
	//TODO
	var sig;
	sig = switch(kind,
		\white, {
			WhiteNoise.ar(amp);
		},
		\pink, {
			PinkNoise.ar(amp);
		},
		\brown, {
			BrownNoise.ar(amp);
		},
		{
			kind.debug("p_noise: ERROR: noise kind not found");
			WhiteNoise.ar(amp);
		}
	);
	sig;

}, [NonControlSpec()]);

Instr(\p_effect, { arg kind, in, fxbus, arg1, arg2, arg3, arg4;
	var sig;
	kind.debug("p_effect: kind");
	sig = switch(kind,
		\reverb, {
			Instr(\p_reverb).value((in:in, mix:arg1, room:arg2, damp:arg3));
		},
		\flanger, {
			Instr(\p_flanger).value((in:in, fbbus:fxbus, mix:arg1, rate:arg2, feedback:arg3, depth:arg4));
		},
		\chorus, {
			Instr(\p_chorus).value((in:in, mix:arg1, rate:arg2, offset:arg3, depth:arg4));
		},
		\phaser, {
			Instr(\p_phaser).value((in:in, fbbus:fxbus, mix:arg1, rate:arg2, feedback:arg3, depth:arg4));
		},
		\delay, {
			Instr(\p_delay).value((in:in, mix:arg1, damp:arg2, delay_left:arg3, delay_right:arg4));
		},
		\eq, {
			Instr(\p_eq).value((in:in, lowshelf:arg1, freq:arg2, boost:arg3, highshelf:arg4));
		},
		{
			kind.debug("p_effect: ERROR: effect kind not found");
			in;
		}
	);
	[in, sig].debug("p_effect: in sig");
	sig;

}, [NonControlSpec(), \audio]);

Instr(\p_ins_effect, { arg kind, in, arg1, arg2;
	var sig;
	kind.debug("p_effect: kind");
	sig = switch(kind,
		\freqshift, {
			Instr(\p_freqshift).value((in:in, mix:arg1, shift:arg2));
		},
		\simpledelay, {
			Instr(\p_simpledelay).value((in:in, mix: arg1, delay:arg2));
		},
		\samplehold, {
			Instr(\p_samplehold).value((in:in, mix: arg1, pitch:arg2));
		},
		\bitcrusher, {
			Instr(\p_bitcrusher).value((in:in, mix: arg1, crush:arg2));
		},
		\simplefilter, {
			Instr(\p_simplefilter).value((in:in, hpfreq:arg1, lpfreq:arg2));
		},
		\sinshaper, {
			Instr(\p_sinshaper).value((in:in, mix: arg1, drive:arg2));
		},
		\parashaper, {
			Instr(\p_parashaper).value((in:in, mix: arg1, drive:arg2));
		},
		\hardclipper, {
			Instr(\p_hardclipper).value((in:in, mix: arg1, drive:arg2));
		},
		{
			kind.debug("p_ins_effect: ERROR: effect kind not found");
			in;
		}
	);
	[in, sig].debug("p_ins_effect: in sig");
	sig;

}, [NonControlSpec(), \audio]);

Instr(\p_freqshift, { arg in, mix, shift;
	var sig;
	sig = FreqShift.ar(in, shift);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_simpledelay, { arg in, mix, delay;
	var sig;
	sig = DelayL.ar(in, delay, delay);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_samplehold, { arg in, mix, pitch;
	var sig;
	var gate;
	gate = LFPulse.ar(pitch);
	sig = Gate.ar(in, gate);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_bitcrusher, { arg in, mix, crush;
	var sig;
	sig = Decimator.ar(in, SampleRate.ir*(crush/31), crush);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_simplefilter, { arg in, lpfreq, hpfreq;
	var sig;
	sig = LPF.ar(in, lpfreq);
	sig = HPF.ar(sig, hpfreq);
}, [\audio]);

Instr(\p_sinshaper, { arg in, mix, drive;
	var sig;
	sig = SineShaper.ar(in, 1-drive);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_parashaper, { arg in, mix, drive;
	var sig;
	//TODO
	sig = SineShaper.ar(in, 1-drive);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_hardclipper, { arg in, mix, drive;
	var sig;
	//TODO
	drive = 1-drive;
	sig = in.clip(0-drive, drive);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_eq, { arg in, lowshelf, freq, boost, highshelf;
	var ou;
	ou = BHiShelf.ar(in, 18000, 1, highshelf);
	ou = BLowShelf.ar(ou, 100, 1, lowshelf);
	ou = BPeakEQ.ar(ou, freq, 1, boost);
	ou
}, [\audio]);

Instr(\p_reverb, { arg in, mix, room, damp;
	FreeVerb.ar(in, mix, room, damp);
}, [\audio]);


Instr(\p_flanger, { arg in, fbbus, mix, rate, feedback, depth;
	var sig;
	var maxdelay = depth;
	var lfo;
	var ou;
	ou = Fb({ arg fb;
		fb = fb * feedback;
	
		lfo = SinOsc.kr(rate).range(0,1)*depth;
		sig = DelayL.ar(in+fb, maxdelay, lfo);
		ou = SelectX.ar(mix, [in, sig]);
	});
	ou;
}, [\audio]);

Instr(\p_chorus, { arg in, mix=0, rate, offset, depth;
	var sig;
	var lfo;
	var delay = [10,15,20,25]/1000;
	lfo = SinOsc.ar(rate).range(0,1) * depth;
	delay = delay * lfo;
	sig = in;
	sig = DelayL.ar(in, delay+depth+0.01, delay+lfo+offset);
	sig = sig.sum;
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_phaser, { arg in, mix=0, rate, feedback, depth;
	var sig;
	var maxdelay = depth;
	var lfo;
	var ou;
	var rq = 1;
	var bands = [200,400,800,1600,4000,8000];
	ou = in + Fb({ arg fb;
		var fbou;
		fb = fb * feedback;

		lfo = SinOsc.kr(rate).range(0,1)*depth;

		fbou = Mix.fill(bands, { arg freq;
			sig = fb;
			sig = BPF.ar(sig, freq, rq);
			sig = DelayL.ar(in, maxdelay, lfo);
		});
		fbou = SelectX.ar(mix, [in, ou]);
		fbou;
	});
	ou;
}, [\audio]);

Instr(\p_delay, { arg in, mix, damp, delay_left, delay_right;
	var sig;
	var sigl, sigr;
	sig = DelayL.ar(in, [delay_left,delay_right], [delay_left, delay_right]);
	sig = LPF.ar(sig, damp);
	SelectX.ar(mix, [in, sig]);
}, [\audio]);

Instr(\p_modulator, { arg kind, steps, internal_mod, gate=1, amp, rate,
		attackTime, releaseTime, phase,
		amp_mod, glidefade_mod,
		curve1, curve2;

	kind.debug("p_modulator");

	switch(kind,
		\lfo, { 
			Instr(\p_lfo).value((
				internal_mod: internal_mod, gate:gate,
				amp:amp, rate:rate, xfade:glidefade_mod, phase:phase, curve1:curve1, curve2:curve2,
				attackTime:attackTime, releaseTime:releaseTime
			)) 
		},
		\stepper, { Instr(\p_stepper).value((steps:steps, amp:amp, rate:rate, amp_mod:amp_mod, glide:glidefade_mod)) },
		\performer, { 
			Instr(\p_performer).value((
				steps:steps, amp:amp, rate:rate, amp_mod:amp_mod, xfade:glidefade_mod, curve1:curve1, curve2:curve2
			)) 
		}
	);
}, [NonControlSpec(), NonControlSpec(), NonControlSpec()]);

Instr(\p_lfo, { arg internal_mod, gate=1, amp, rate, xfade, phase, curve1, curve2, attackTime, releaseTime;
	var sig1, sig2, sig;
	var env;

	env = EnvGen.ar(Env.perc(attackTime, releaseTime, 1), gate);

	if(internal_mod.notNil and: {internal_mod[\dest].notNil}) {
		switch(internal_mod[\dest],
			\rate, {
				rate = rate + (rate * env * internal_mod[\range]);
			},
			\xfade, {
				xfade = xfade + (xfade * env * internal_mod[\range]);
			},
			\amp, {
				amp = amp + (amp * env * internal_mod[\range]);
			}
		);
	};

	sig1 = Osc.ar(curve1, rate, phase);
	sig2 = Osc.ar(curve2, rate, phase);
	sig = SelectX.ar(xfade, [sig2, sig1]);
	sig = sig * amp;
}, [NonControlSpec()]);


Instr(\p_stepper, { arg steps, amp, rate, amp_mod, glide;
	var seq, trig, amp_seq, glide_seq;
	var sig;
	steps = steps ?? (
		amp: [1.0, 0.5, 0.0, 1.0],
		ampmod: [1,0,0,1],
		glidefade: [1,0,1,0],
	);

	trig = Impulse.ar(rate);

	seq = Dseq(steps[\amp],inf);

	glide_seq = Dseq(steps[\glidefade],inf);
	glide = Demand.ar(trig, 0, glide_seq) * glide;

	amp_seq = Dseq(steps[\ampmod],inf);
	amp_mod = Select.ar(Demand.ar(trig, 0, amp_seq), [DC.ar(1), K2A.ar(amp_mod)]);

	sig = Demand.ar(trig, 0, seq);
	sig = Lag.ar(sig, glide);
	sig = sig * amp_mod * amp;

}, [NonControlSpec()]);

Instr(\p_performer, { arg steps, amp, rate, amp_mod, xfade, curve1, curve2;
	var sig1, sig2, sig;
	var trig, amp_seq, xfade_seq;
	var scale1 = BufFrames.kr(curve1)/SampleRate.ir;
	var scale2 = BufFrames.kr(curve2)/SampleRate.ir;

	steps = steps ?? (
		amp: [1.0, 0.5, 0.0, 1.0],
		ampmod: [1,0,0,1],
		glidefade: [1,0,1,0],
	);

	trig = Impulse.ar(rate);

	amp_seq = Dseq(steps[\ampmod],inf);
	amp_mod = Select.ar(Demand.ar(trig, 0, amp_seq), [DC.ar(1), K2A.ar(amp_mod)]);

	xfade_seq = Dseq(steps[\glidefade],inf);
	xfade = xfade * Demand.ar(trig, 0, xfade_seq);

	sig1 = BufRd.ar(1, curve1, Phasor.ar(0, rate*scale1, 0, BufFrames.kr(curve1)));
	sig2 = BufRd.ar(1, curve2, Phasor.ar(0, rate*scale2, 0, BufFrames.kr(curve2)));

	sig = SelectX.ar(xfade, [sig2, sig1]);
	sig = sig * amp_mod * amp;
}, [NonControlSpec()]);

Instr(\p_filter, { arg in, kind, arg1, arg2, arg3, ffreq;
	var sig;
	kind.debug("p_filter: kind");
	sig = switch(kind,
		\lpf, {
			arg1 = (arg1+ffreq.cpsmidi).midicps;
			LPF.ar(in, arg1);
		},
		\bpf, {
			arg1 = (arg1+ffreq.cpsmidi).midicps;
			BPF.ar(in, arg1, arg2)
		},
		\hpf, {
			arg1 = (arg1+ffreq.cpsmidi).midicps;
			HPF.ar(in, arg1)
		},
		\rlpf, {
			arg1 = (arg1+ffreq.cpsmidi).midicps;
			RLPF.ar(in, arg1, arg2)
		},
		\rhpf, {
			arg1 = (arg1+ffreq.cpsmidi).midicps;
			RHPF.ar(in, arg1, arg2)
		},
		\comb, {
			CombL.ar(in, arg1, arg2, arg3);
		}
	);
	sig;

},[\audio, NonControlSpec()]);

Instr(\p_oscillator, { arg wt_range=0, amp=0.1, freq=200, wt=0, detune=0.0, wt_position=0, intensity=1;
	var ou, endfreq;
	endfreq = (freq.cpsmidi + detune).midicps;
	if(wt_range == 0) {
		ou = Osc.ar(wt, endfreq);
	} {
		ou = VOsc.ar(wt+(wt_position.clip(0,wt_range)), endfreq);
	};
	[freq, detune, amp].debug("p_oscillator: frq, detune, amp");
	ou = ou * amp;
}, [NonControlSpec()]);


//Instr(\massive).addSynthDef(nil, [
//	(
//		\filter2: \rlpf
//	)
//]);