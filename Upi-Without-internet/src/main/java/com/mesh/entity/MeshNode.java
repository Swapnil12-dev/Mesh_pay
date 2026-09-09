/**
 * Represents a device participating in the MeshPay network.
 *
 * Every smartphone running the MeshPay application acts
 * as a mesh node and can perform one of the following roles:
 *
 * - Sender Node
 * - Relay Node
 * - Bridge Node
 *
 * Responsibilities:
 * - Stores device information.
 * - Tracks connectivity state.
 * - Tracks internet availability.
 * - Helps visualize packet routing through the mesh network.
 */


package com.mesh.entity;
import lombok.*;
import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mesh_nodes")
public class MeshNode {
	
	   	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true, nullable = false)
	    private String nodeId;

	    @Column(nullable = false)
	    private String userVpa;

	    @Column(nullable = false)
	    private String deviceName;

	    @Column(nullable = false)
	    private boolean internetAvailable;

	    @Column(nullable = false)
	    private boolean bluetoothEnabled;

	    @Column(nullable = false)
	    private Instant lastSeen;
	    
	    @Enumerated(EnumType.STRING)
	    private NodeRole role;
	    public enum NodeRole {
	        RELAY,
	        BRIDGE
	    }

}
