package org.pstcl.ea.entity.mapping;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.pstcl.ea.entity.LocationMaster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="map_month_lossreport_location",uniqueConstraints = {@UniqueConstraint(columnNames = {"LOC_ID", "month","year"})})
public class MapMonthLossReportLocation implements Cloneable{

	@Id
	@GeneratedValue
	private Integer id;

	@Column
	private Integer month;

	@Column
	private Integer year;

	@ManyToOne
	@JoinColumn(name = "LOC_ID")
	private LocationMaster location;

	@Column
	private String lossReportCriteria;
	
	@Column
	private Integer lossReportInclusion;
	
	@Column
	private Integer lossReportOrder;


@Override
public Object clone() throws CloneNotSupportedException {
	return (MapMonthLossReportLocation)super.clone();
}





}
