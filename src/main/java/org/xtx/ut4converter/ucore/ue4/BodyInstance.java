/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import org.xtx.ut4converter.t3d.T3DUtils;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

/**
 * Unreal Engine 4 (UE3 as well ?) very 'light' implementation of body instance
 * 
 * @author XtremeXp
 */
public class BodyInstance {

	/**
	 * TODO check other collision profiles
	 */
	public String collisionProfileName = "Custom";

	public Vector3d scale3D = new Vector3d(1d, 1d, 1d);

	private final List<CollisionResponse> collisionResponses = new ArrayList<>();

	private final CollisionEnabled collisionEnabled = CollisionEnabled.QueryAndPhysics;


	public enum CollisionEnabled {
		NoCollision, QueryOnly, QueryAndPhysics
	}

	public enum ECollisionChannel {
		WorldStatic, WorldDynamic, Pawn, Visibility, Camera, PhysicsBody, Vehicle, Destructible,
		// UT4 Custom
		Weapon, WeaponNoCharacter
    }

    /**
	 * Collision Response when attached actor to this body instance collides
	 * with another ones
	 */
	public enum ECollisionResponse {
		ECR_Ignore, ECR_Overlap, ECR_Block
	}

	public BodyInstance() {
		initialize();
	}

	/**
	 * Set collision for all channels
	 */
	private void initialize() {
		CollisionResponse cre = new CollisionResponse();

		for (ECollisionChannel channel : ECollisionChannel.values()) {
			cre.responseArray.add(new ResponseChannel(channel, ECollisionResponse.ECR_Block));
		}

		collisionResponses.add(cre);
	}

	static class ResponseChannel {
		ECollisionChannel channel;
		ECollisionResponse response;

		public ResponseChannel(ECollisionChannel channel, ECollisionResponse response) {
			this.channel = channel;
			this.response = response;
		}

		public void toT3d(StringBuilder sb) {
			sb.append("Channel=\"").append(channel.name()).append("\",Response=").append(response.name());
		}
	}

	static class CollisionResponse {
		List<ResponseChannel> responseArray = new ArrayList<>();

		public void toT3d(StringBuilder sb) {

			responseArray.forEach((rc) -> {
				sb.append("(");
				rc.toT3d(sb);
				sb.append("),");
			});

			sb.deleteCharAt(sb.length() - 1);
		}

		public void setCollisionResponse(ECollisionResponse cr) {
			responseArray.forEach((rc) -> {
				rc.response = cr;
			});
		}
	}

	/**
	 * Force collision response for this body instance
	 * 
	 * @param cr
	 *            Collision response
	 */
	public void setCollisionResponse(ECollisionResponse cr) {

		collisionProfileName = "Custom";

		collisionResponses.forEach((cre) -> {
			cre.setCollisionResponse(cr);
		});
	}

	/**
	 * Write to t3d this bodyinstance
	 * 
	 * @param sb
	 *            Current string builder
	 */
	public void toT3d(StringBuilder sb) {

		sb.append("BodyInstance=(");

		if (scale3D != null) {
			sb.append("Scale3D=").append(T3DUtils.toStringVec(scale3D)).append(",");
		}

		sb.append("CollisionProfileName=\"").append(collisionProfileName).append("\",");
		sb.append("CollisionEnabled=\"").append(collisionEnabled.name()).append("\",");

		if (collisionProfileName.equals("Custom") && !collisionResponses.isEmpty()) {
				sb.append("CollisionResponses=(ResponseArray=(");

				collisionResponses.forEach((cr) -> {
					cr.toT3d(sb);
				});

				sb.append("))");
		}

		sb.append(")");
	}
}
