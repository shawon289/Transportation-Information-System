package com.transportinfo.webbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BUS_INFO_MODEL")
public class BusInfoModel {
    @Id
    private String id;

    private String busName;

    private String busNo;

    private String busInfo;

    private String from;

    private String to;

    private String
}
